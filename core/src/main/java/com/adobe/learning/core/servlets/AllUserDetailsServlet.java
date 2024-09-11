package com.adobe.learning.core.servlets;

import com.adobe.learning.core.service.AEMUserAndGroupDetails;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that take input as UserName or GroupName and Write all Email Address.
 * http://localhost:4502/bin/userDetails?userOrGroup=admin
 */

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Page Programmatically Description",
        "sling.servlet.paths=" + "/bin/userDetails", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class AllUserDetailsServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AllUserDetailsServlet.class);

    @Reference
    AEMUserAndGroupDetails aemUserAndGroupDetails;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {

            // Get content fragment name from the request object
            String userOrGroup = request.getParameter("userOrGroup");

            JSONObject userDetails = aemUserAndGroupDetails.getAllUserDetails(userOrGroup);

            JSONArray groupDetails = aemUserAndGroupDetails.getAllGroupDetails(userOrGroup);

            response.getWriter().write("User Details :" + userDetails.toString() + "\n");

            response.getWriter().write("Group Details :" + groupDetails.toString());

        } catch (NullPointerException | IOException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }

}
