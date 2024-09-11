package com.adobe.learning.core.workflow;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Shiv Prakash
 */

@Component(service = WorkflowProcess.class, property = {Constants.SERVICE_DESCRIPTION + "= Backup Package Creation Step",
        Constants.SERVICE_VENDOR + "= workflow.com",
        "process.label" + "= Backup Package Creation Step"})
public class BackupPackageCreationProcess implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(BackupPackageCreationProcess.class);

    Session session;
    ResourceResolver resourceResolver;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    String packagePath = StringUtils.EMPTY;
    String packageName = StringUtils.EMPTY;
    List<String> filterPaths;
    String groupName = StringUtils.EMPTY;
    JcrPackage jcrNodePackage;
    JcrPackage jcrPackage;
    JcrPackage backupPackage;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            List<HistoryItem> historyItemList = workflowSession.getHistory(workItem.getWorkflow());
            MetaDataMap historyMetaDataMap = historyItemList.get(historyItemList.size() - 1).getWorkItem().getMetaDataMap();
            packagePath = historyMetaDataMap.get("migratedPackage").toString();
            Session session = workflowSession.getSession();
            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));

            JcrPackageManager jcrPackageManager = PackagingService.getPackageManager(session);
            Node packageNode = Objects.requireNonNull(resourceResolver.getResource(packagePath)).adaptTo(Node.class);
            assert packageNode != null;


            jcrNodePackage = jcrPackageManager.open(packageNode);
            assert jcrNodePackage != null;
            JcrPackageDefinition jcrNodeDefinition = jcrNodePackage.getDefinition();
            assert jcrNodeDefinition != null;
            packageName = jcrNodeDefinition.get("name");
            groupName = jcrNodeDefinition.get("group");

            List<PathFilterSet> pathFilterSetList = Objects.requireNonNull(jcrNodeDefinition.getMetaInf().getFilter()).getFilterSets();
            filterPaths = new ArrayList<>();
            for (PathFilterSet currentFilter : pathFilterSetList) {
                filterPaths.add(currentFilter.getRoot());
            }

            String backupPackagePath = packagePath.substring(0, packagePath.lastIndexOf("/")) + "/" + packageName + "_Backup.zip";
            Resource backupPackageResource = resourceResolver.getResource(backupPackagePath);

            if (backupPackageResource == null) {
                jcrPackage = jcrPackageManager.create(groupName, packageName + "_Backup");
                JcrPackageDefinition definition = jcrPackage.getDefinition();
                DefaultWorkspaceFilter filter = new DefaultWorkspaceFilter();
                for (String filterPath : filterPaths) {
                    PathFilterSet pathFilterSet = new PathFilterSet();
                    pathFilterSet.setRoot(filterPath);
                    filter.add(pathFilterSet);
                }

                assert definition != null;
                definition.setFilter(filter, true);

                ProgressTrackerListener listener = new DefaultProgressListener();
                jcrPackageManager.assemble(jcrPackage, listener);

            } else {
                Node backupNode = backupPackageResource.adaptTo(Node.class);
                assert backupNode != null;
                backupPackage = jcrPackageManager.open(backupNode);
                ProgressTrackerListener backupListener = new DefaultProgressListener();
                assert backupPackage != null;
                jcrPackageManager.assemble(backupPackage, backupListener);
            }

        } catch (LoginException | RepositoryException | IOException | PackageException e) {
            logger.error("Exception Occurred in Replication {}", e.getMessage());
        } finally {
            if (jcrNodePackage != null)
                jcrNodePackage.close();
            if (jcrPackage != null)
                jcrPackage.close();
            if (backupPackage != null)
                backupPackage.close();
        }
    }
}