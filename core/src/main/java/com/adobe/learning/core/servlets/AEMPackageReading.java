package com.adobe.learning.core.servlets;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackagingService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
 * http://localhost:4502/bin/packageReading?packagePath=/etc/packages/my_packages/sample.zip
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Reading",
        "sling.servlet.paths=" + "/bin/packageReading", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class AEMPackageReading extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(AEMPackageReading.class);

    ResourceResolver resourceResolver;

    String packageName = StringUtils.EMPTY;
    List<String> filterPaths;
    String groupName = StringUtils.EMPTY;
    JsonObject jsonObject;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // Getting package path from query parameter
        String packagePath = request.getParameter("packagePath");

        //Getting resource resolver and session
        resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);

        //Getting JcrPackageManager with the help of session
        JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);

        //Getting the Node from Package
        Node packageNode = Objects.requireNonNull(resourceResolver.getResource(packagePath)).adaptTo(Node.class);
        assert packageNode != null;

        jsonObject = new JsonObject();

        //Get JcrPackage with the help of Package Node
        try (JcrPackage jcrNodePackage = jcrPackageManager.open(packageNode)) {
            assert jcrNodePackage != null;
            //Getting Package Definition
            JcrPackageDefinition jcrNodeDefinition = jcrNodePackage.getDefinition();
            assert jcrNodeDefinition != null;
            //Getting Package and Group Name
            packageName = jcrNodeDefinition.get("name");
            groupName = jcrNodeDefinition.get("group");
            jsonObject.addProperty("name", packageName);
            jsonObject.addProperty("group", groupName);

            //Getting Path Filter Set from Package definition using getMetaImf Method
            List<PathFilterSet> pathFilterSetList = Objects.requireNonNull(jcrNodeDefinition.getMetaInf().getFilter()).getFilterSets();
            filterPaths = new ArrayList<>();
            //Adding all the filter in array list
            for (PathFilterSet currentFilter : pathFilterSetList) {
                filterPaths.add(currentFilter.getRoot());
            }
            jsonObject.addProperty("filter", filterPaths.toString());

            response.getWriter().write("Package Details : \n");
            response.getWriter().write(jsonObject.toString());

        } catch (Exception e) {
            logger.error("Error in Get Drop Down Values {}", e.getMessage());
        }
    }
}
