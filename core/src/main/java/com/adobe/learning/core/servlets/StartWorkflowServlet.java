package com.adobe.learning.core.servlets;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Objects;

/*
http://localhost:8080/bin/learning/triggerWorkflow?pagePath=/content/learning/us/en
 */

@Component(service = Servlet.class, property = {"sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/learning/triggerWorkflow"
})
public class StartWorkflowServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 7762806638577908286L;
    private static final Logger logger = LoggerFactory.getLogger(StartWorkflowServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            // Get the payload path from the request
            String payloadPath = request.getParameter("pagePath");

            if (!StringUtils.isEmpty(payloadPath)) {

                // Getting the resource resolver
                final ResourceResolver resourceResolver = request.getResourceResolver();

                // Get the workflow session from the resource resolver
                final WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);

                // Workflow model path - This is the already created workflow
                final String model = "/var/workflow/models/version-creation";

                // Get the workflow model object
                final WorkflowModel workflowModel = Objects.requireNonNull(workflowSession).getModel(model);

                // Create a workflow Data (or Payload) object pointing to a resource via JCR
                // Path (alternatively, a JCR_UUID can be used)
                final WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payloadPath);

                // Start the workflow!
                workflowSession.startWorkflow(workflowModel, workflowData);

                logger.info("Workflow: {} started", model);
                response.getWriter().println("Workflow Executed");
            } else {
                response.getWriter().println("Payload path is not present in the query parameter");
            }
        } catch (WorkflowException | IOException e) {
            logger.error("Exception occurred: {}", e.getMessage());
        }

    }
}
