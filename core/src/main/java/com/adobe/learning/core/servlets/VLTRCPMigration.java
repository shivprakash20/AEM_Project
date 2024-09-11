package com.adobe.learning.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Shiv
 * http://localhost:4502/bin/vltRcPMigration?packagePath=/etc/packages/my_packages/sample.zip
 */

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Migrating Using VLT RCP",
        "sling.servlet.paths=" + "/bin/vltRcPMigration", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class VLTRCPMigration extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(VLTRCPMigration.class);

    String packageName = StringUtils.EMPTY;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // Getting package path from query parameter
        String packagePath = request.getParameter("packagePath");

        try {
            //Curl Command for Uploading the Package
            ProcessBuilder processBuilder = new ProcessBuilder("./vlt", "rcp", "-b", "100", "-e", "-r", "-q", "-u", "http://admin:admin@localhost:4502/crx/-/jcr:root/content/learning/us/en/vlt-rcp-page", "http://admin:admin@localhost:4503/crx/-/jcr:root/content/learning/us/en/vlt-rcp-page");
            Process process = processBuilder.start();
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            logger.info(result);
            int exitCode = process.exitValue();
            process.destroy();

        } catch (Exception e) {
            logger.error("Error in VLT RCP Execution {}", e.getMessage());
        }
    }
}
