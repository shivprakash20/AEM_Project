package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/**
 * @author Shiv
 * http://localhost:4502/bin/packageFTPUpload
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= FTP Package Upload",
        "sling.servlet.paths=" + "/bin/packageFTPUpload", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class AEMFTPUpload extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(AEMFTPUpload.class);
    private static final String HOST = "";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";

    private URLConnection urlConnection;

    ResourceResolver resourceResolver;
    String packagePath = "C:/Users/Lenovo/Documents/sample.zip";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            URL url = new URL("http://localhost:4502/crx/packmgr/service/.json/?cmd=upload");
            urlConnection = url.openConnection();
            String userPassword = USER + ":" + PASSWORD;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userPassword.getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);

        } catch (Exception e) {
            logger.error("Error in URL Connection {}", e.getMessage());
        }
    }
}
