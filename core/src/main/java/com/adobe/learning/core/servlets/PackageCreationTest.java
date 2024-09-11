package com.adobe.learning.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.fs.io.ImportOptions;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Shiv
 * http://localhost:4502/bin/packageCreationTest?packagePath=/etc/packages/my_packages/A.zip
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Creation Test",
        "sling.servlet.paths=" + "/bin/packageCreationTest", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class PackageCreationTest extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(PackageCreationTest.class);

    ResourceResolver resourceResolver;

    String packageName = StringUtils.EMPTY;
    List<String> filterPaths;
    String groupName = StringUtils.EMPTY;
    JcrPackage jcrPackage;
    JcrPackage backupPackage;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // 1st Step : Package Reading and getting all payload from filter
        String packagePath = request.getParameter("packagePath");

        //Getting resource resolver and session
        resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);

        //Getting JcrPackageManager with the help of session
        JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);

        //Getting the Node from Package
        Node packageNode = Objects.requireNonNull(resourceResolver.getResource(packagePath)).adaptTo(Node.class);
        assert packageNode != null;

        //Get JcrPackage with the help of Package Node
        try (JcrPackage jcrNodePackage = jcrPackageManager.open(packageNode)) {
            assert jcrNodePackage != null;
            //Getting Package Definition
            JcrPackageDefinition jcrNodeDefinition = jcrNodePackage.getDefinition();
            assert jcrNodeDefinition != null;
            //Getting Package and Group Name
            packageName = jcrNodeDefinition.get("name");
            groupName = jcrNodeDefinition.get("group");

            //Getting Path Filter Set from Package definition using getMetaImf Method
            List<PathFilterSet> pathFilterSetList = Objects.requireNonNull(jcrNodeDefinition.getMetaInf().getFilter()).getFilterSets();
            filterPaths = new ArrayList<>();
            //Adding all the filter in array list
            for (PathFilterSet currentFilter : pathFilterSetList) {
                filterPaths.add(currentFilter.getRoot());
            }

        /*For 'create' method the parameter packageGroup is optional
            we can give group name under which the package should be created else it will take default,
            packageName is the name of the package and 1.0 is the version of the package
        */
            String backupPackagePath = packagePath.substring(0, packagePath.lastIndexOf("/")) + "/" + packageName + "_Backup.zip";
            Resource backupPackageResource = resourceResolver.getResource(backupPackagePath);
            //Getting the backup resource and checking if the package is already existing
            if (backupPackageResource == null) {
                //Create a backup package manager if no package exist
                jcrPackage = jcrPackageManager.create(groupName, packageName + "_Backup");
                JcrPackageDefinition definition = jcrPackage.getDefinition();
                //Getting DefaultWorkspaceFilter and setting up filter
                DefaultWorkspaceFilter filter = new DefaultWorkspaceFilter();
                /*filterPaths is the List containing the list of paths*/
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

            } else {
                //This assembles method will build the package
                Node backupNode = backupPackageResource.adaptTo(Node.class);
                assert backupNode != null;
                backupPackage = jcrPackageManager.open(backupNode);
                ProgressTrackerListener backupListener = new DefaultProgressListener();
                assert backupPackage != null;
                jcrPackageManager.assemble(backupPackage, backupListener);
            }

            //Method to install the Package and Specify the import configurations
            ImportOptions importOption = new ImportOptions();
            jcrNodePackage.install(importOption);
        } catch (Exception e) {
            logger.error("Error in Get Drop Down Values {}", e.getMessage());
        }finally {
            if (jcrPackage != null)
                jcrPackage.close();
            if (backupPackage != null)
                backupPackage.close();
        }
    }
}
