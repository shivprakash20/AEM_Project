package com.adobe.learning.core.servlets;

import com.day.cq.replication.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;

/**
 * @author Vishnu
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Replication Test",
        "sling.servlet.paths=" + "/bin/packageTest", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class PackageTest extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(PackageTest.class);

    ResourceResolver resourceResolver;

    @Reference
    AgentManager agentManager;

    @Reference
    Replicator replicator;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);
            String replicationAgentName = "Package Replication Agent";
            String payloadPath = "/etc/packages/my_packages/LearningPanel.zip";

            for (final Agent replicationAgent : agentManager.getAgents().values()) {
                if (replicationAgentName.equals(replicationAgent.getConfiguration().getName())) {
                    ReplicationOptions replicationOptions = new ReplicationOptions();
                    replicationOptions.setFilter(agent -> replicationAgentName.equals(replicationAgent.getConfiguration().getName()));

                    /* PayLoad Replication*/
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, payloadPath, replicationOptions);
                }
            }

        } catch (Exception e) {
            logger.error("Error in Get Drop Down Values", e);
        }
    }
}
