package com.adobe.learning.core.workflow;

import com.adobe.learning.core.service.config.CurlCommandHostConfig;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Vishu
 */

@Component(service = WorkflowProcess.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Migration Step",
        Constants.SERVICE_VENDOR + "= workflow.com",
        "process.label" + "= Package Migration Step"})
@Designate(ocd = CurlCommandHostConfig.class)
public class PackageMigrationStep implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(PackageMigrationStep.class);

    String packagePath = StringUtils.EMPTY;

    @Reference
    SlingSettingsService slingSettingsService;

    CurlCommandHostConfig curlCommandHostConfig;

    @Activate
    protected void activate(CurlCommandHostConfig config) {
        this.curlCommandHostConfig = config;
    }

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        try {
            List<HistoryItem> historyItemList = workflowSession.getHistory(workItem.getWorkflow());
            MetaDataMap historyMetaDataMap = historyItemList.get(historyItemList.size() - 1).getWorkItem().getMetaDataMap();

            if (historyMetaDataMap.containsKey("packageDropdown"))
                packagePath = historyMetaDataMap.get("packageDropdown").toString();

            if (!packagePath.isEmpty()) {
                String homePath = slingSettingsService.getSlingHomePath();
                String packageQuickStartPath = homePath + packagePath.substring(packagePath.lastIndexOf("/"));

                // Curl for downloading the package
                ProcessBuilder downloadBuilder = new ProcessBuilder("curl", "-u", curlCommandHostConfig.sender_User_Name() + ":" + curlCommandHostConfig.sender_User_Password(), curlCommandHostConfig.sender_Host_URL() + packagePath, "-o", packageQuickStartPath);
                Process downloadProcess = downloadBuilder.start();
                downloadProcess.waitFor();
                int downloadExitCode = downloadProcess.exitValue();
                logger.info("Exit Code : {}", downloadExitCode);
                downloadProcess.destroy();

                //Curl Command for Uploading the Package
                String[] command = {"curl", "-u", curlCommandHostConfig.receiver_User_Name() + ":" + curlCommandHostConfig.receiver_User_Password(), "-F", "cmd=upload", "-F", "force=true", "-F", "package=@\"" + packageQuickStartPath + "\"", curlCommandHostConfig.receiver_Host_URL() + "/crx/packmgr/service/.json"};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                process.waitFor();
                int exitCode = process.exitValue();
                logger.info("Exit Code : {}", exitCode);
                process.destroy();

                //Removing file from QuickStart Folder
                File packageFile = new File(packageQuickStartPath);
                boolean packageStatus = packageFile.delete();
                logger.info("Package Delete Status : {}", packageStatus);
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Exception Occurred in Curl Command Execution {}", e.getMessage());
        }
    }
}