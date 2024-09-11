package com.adobe.learning.core.workflow;

import com.day.cq.replication.*;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;

/**
 * @author Shiv Prakash
 */

@Component(service = WorkflowProcess.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Replication Step",
        Constants.SERVICE_VENDOR + "= workflow.com",
        "process.label" + "= Package Replication Step"})
public class PackageReplicationStep implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(PackageReplicationStep.class);

    @Reference
    Replicator replicator;

    @Reference
    AgentManager agentManager;

    Session session;

    String replicationAgentName = StringUtils.EMPTY;
    String payloadPath = StringUtils.EMPTY;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        try {
            session = workflowSession.getSession();

            // To get argument in process class
            replicationAgentName = metaDataMap.get("PROCESS_ARGS", String.class);

            List<HistoryItem> historyItemList = workflowSession.getHistory(workItem.getWorkflow());
            MetaDataMap historyMetaDataMap = historyItemList.get(historyItemList.size() - 1).getWorkItem().getMetaDataMap();

            if (historyMetaDataMap.containsKey("packageDropdown"))
                payloadPath = historyMetaDataMap.get("packageDropdown").toString();

            for (final Agent replicationAgent : agentManager.getAgents().values()) {
                if (replicationAgentName.equals(replicationAgent.getConfiguration().getName())) {
                    ReplicationOptions replicationOptions = new ReplicationOptions();
                    replicationOptions.setFilter(agent -> replicationAgentName.equals(agent.getConfiguration().getName()));

                    /* PayLoad Replication*/
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, payloadPath, replicationOptions);
                    logger.info("Replicated via Agent : {} for Payload {}", replicationAgentName, payloadPath);
                }
            }

        } catch (ReplicationException e) {
            logger.error("Exception Occurred in Replication {}", e.getMessage());
        }
    }
}