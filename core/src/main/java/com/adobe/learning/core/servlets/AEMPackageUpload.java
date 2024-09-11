package com.adobe.learning.core.servlets;

import org.apache.jackrabbit.vault.packaging.JcrPackage;
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

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.File;
import java.util.Objects;

/**
 * @author Shiv
 * http://localhost:4502/bin/packageUpload
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Upload",
        "sling.servlet.paths=" + "/bin/packageUpload", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class AEMPackageUpload extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(AEMPackageUpload.class);

    ResourceResolver resourceResolver;

    String packagePath = "C:/Users/Lenovo/Documents/sample.zip";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            //Getting resource resolver and session
            resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);

            //Getting JcrPackageManager with the help of session
            JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);
            //Package upload with help of JcrPackageManager
            
            JcrPackage inputPackage = jcrPackageManager.upload(new File(packagePath), false, true, "sample.zip");
            String createdPackage = Objects.requireNonNull(inputPackage.getDefinition()).get("name");
            response.getWriter().write("Created Package Name : " + createdPackage);
        } catch (Exception e) {
            logger.error("Error in Get Drop Down Values {}", e.getMessage());
        }
    }
}
