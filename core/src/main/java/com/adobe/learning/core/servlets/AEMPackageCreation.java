package com.adobe.learning.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.fs.io.ImportOptions;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackagingService;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shiv
 * http://localhost:4502/bin/packageCreation?packageName=sample&groupName=my_packages
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Creation",
        "sling.servlet.paths=" + "/bin/packageCreation", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class AEMPackageCreation extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(AEMPackageCreation.class);

    ResourceResolver resourceResolver;

    String packageName = StringUtils.EMPTY;
    private List<String> filterPaths;
    String groupName = StringUtils.EMPTY;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // Getting package name & group name from query parameter
        packageName = request.getParameter("packageName");
        groupName = request.getParameter("groupName");

        //Setting up the filter pages for package
        filterPaths = new ArrayList<>();
        filterPaths.add("/content/learning/us/en/samplePage");
        filterPaths.add("/content/learning/us/en/samplePage");


        //Getting resource resolver and session
        resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);

        //Getting JcrPackageManager with the help of session
        JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);

        //Creating JcrPackage with the help of name and group
        try (JcrPackage jcrPackage = jcrPackageManager.create(groupName, packageName)) {
            //Getting JcrPackageDefinition
            JcrPackageDefinition definition = jcrPackage.getDefinition();
            //Getting DefaultWorkspaceFilter and setting up filter
            DefaultWorkspaceFilter filter = new DefaultWorkspaceFilter();
            /*filterPaths is the package filters*/
            for (String filterPath : filterPaths) {
                PathFilterSet pathFilterSet = new PathFilterSet();
                pathFilterSet.setRoot(filterPath);
                filter.add(pathFilterSet);
            }

            //if autoSave is false then we have to explicitly save the session.
            assert definition != null;
            definition.setFilter(filter, true);

            //This method will build the package.
            ProgressTrackerListener listener = new DefaultProgressListener();
            jcrPackageManager.assemble(jcrPackage, listener);

            //Method to install the Package and Specify the import configurations
            ImportOptions importOption = new ImportOptions();
            jcrPackage.install(importOption);

            response.getWriter().write("Package created successfully !!!");

        } catch (Exception e) {
            logger.error("Error in Package Creation {}", e.getMessage());
        }
    }
}
