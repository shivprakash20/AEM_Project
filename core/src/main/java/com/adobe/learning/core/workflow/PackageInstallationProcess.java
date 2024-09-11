package com.adobe.learning.core.workflow;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.io.ImportOptions;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Shiv Prakash
 */

@Component(service = WorkflowProcess.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Installation Process Step",
        Constants.SERVICE_VENDOR + "= workflow.com",
        "process.label" + "= Package Installation Process Step"})
public class PackageInstallationProcess implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(PackageInstallationProcess.class);

    Session session;
    ResourceResolver resourceResolver;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    String packagePath = StringUtils.EMPTY;
    JcrPackage jcrNodePackage;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            List<HistoryItem> historyItemList = workflowSession.getHistory(workItem.getWorkflow());
            MetaDataMap historyMetaDataMap = historyItemList.get(historyItemList.size() - 4).getWorkItem().getMetaDataMap();
            packagePath = historyMetaDataMap.get("migratedPackage").toString();
            session = workflowSession.getSession();
            resourceResolver = resourceResolverFactory.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));

            JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);
            Node packageNode = Objects.requireNonNull(resourceResolver.getResource(packagePath)).adaptTo(Node.class);
            assert packageNode != null;

            jcrNodePackage = jcrPackageManager.open(packageNode);
            assert jcrNodePackage != null;

            ImportOptions importOption = new ImportOptions();
            jcrNodePackage.install(importOption);

        } catch (LoginException | PackageException | RepositoryException | IOException e) {
            logger.error("Exception Occurred in Replication {}", e.getMessage());
        } finally {
            if (jcrNodePackage != null)
                jcrNodePackage.close();
        }
    }
}