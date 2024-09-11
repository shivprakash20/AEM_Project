package com.adobe.learning.core.servlets;

import com.adobe.learning.core.service.config.CurlCommandHostConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Shiv
 * http://localhost:4502/bin/cUrlPackageUpload?packagePath=/etc/packages/my_packages/sample.zip
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package upload using cUrl",
        "sling.servlet.paths=" + "/bin/cUrlPackageUpload", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
@Designate(ocd = CurlCommandHostConfig.class)
public class CurlPackageUploading extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(CurlPackageUploading.class);

    String packageName = StringUtils.EMPTY;

    @Reference
    SlingSettingsService slingSettingsService;

    CurlCommandHostConfig curlCommandHostConfig;

    @Activate
    protected void activate(CurlCommandHostConfig config){this.curlCommandHostConfig = config;}

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        // Getting package path from query parameter
        String packagePath = request.getParameter("packagePath");
        String homePath = slingSettingsService.getSlingHomePath();
        packageName = packagePath.substring(packagePath.lastIndexOf("/") + 1);
        String packageQuickStartPath = homePath + "/" + packageName;

        try {
            // Curl for downloading the package
            ProcessBuilder downloadBuilder = new ProcessBuilder("curl", "-u", "admin:admin", curlCommandHostConfig.sender_Host_URL() + "/etc/packages/my_packages/sample.zip", "-o", packageQuickStartPath);
            Process downloadProcess = downloadBuilder.start();
            downloadProcess.waitFor();
            BufferedReader downloadReader = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()));
            StringBuilder localBuilder = new StringBuilder();
            String downloadLine = null;
            while ((downloadLine = downloadReader.readLine()) != null) {
                localBuilder.append(downloadLine);
                localBuilder.append(System.getProperty("line.separator"));
            }
            String downloadResult = localBuilder.toString();
            int downloadExitCode = downloadProcess.exitValue();
            logger.info("Process Result : {}, Exit Code : {}", downloadResult, downloadExitCode);
            downloadProcess.destroy();


            //Curl Command for Uploading the Package
            String[] command = {"curl", "-u", "admin:admin", "-F", "cmd=upload", "-F", "force=true", "-F", "package=@\"C:/Users/Lenovo/Documents/sample.zip\"", curlCommandHostConfig.receiver_Host_URL() + "/crx/packmgr/service/.json"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            process.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.getProperty("line.separator"));
            }
            String processResult = stringBuilder.toString();
            int exitCode = process.exitValue();
            logger.info("Process Result : {}, Exit Code : {}", processResult, exitCode);
            process.destroy();

            //Removing file from QuickStart Folder
            File packageFile = new File(packageQuickStartPath);
            boolean packageStatus = packageFile.delete();
            logger.info("Package Delete Status : {}", packageStatus);

        } catch (Exception e) {
            logger.error("Error in Curl Execution {}", e.getMessage());
        }
    }
}
