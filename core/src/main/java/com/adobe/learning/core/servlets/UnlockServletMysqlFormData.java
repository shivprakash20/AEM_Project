package com.adobe.learning.core.servlets;

import com.adobe.learning.core.service.UnLockServletFormData;
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
 * Servlet that Fetch and Update data in MYSQL.
 * http://localhost:4502/bin/unlockServlet
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= AEM Servlet to Fetch and Store Data in MYSQL",
        "sling.servlet.paths=" + "/bin/unlockServlet", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class UnlockServletMysqlFormData extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(UnlockServletMysqlFormData.class);

    @Reference
    UnLockServletFormData unLockServletFormData;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            LOGGER.debug("### Inside MYSQL Data Servlet");
            if (request.getParameter("operation").equalsIgnoreCase("fetch")) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (request.getParameterMap().containsKey("userId")) {
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    JSONObject userData = unLockServletFormData.getUserData(userId);
                    if (userData != null) {
                        response.getWriter().write(userData.toString());
                    } else {
                        response.getWriter().write("");
                    }
                } else {
                    JSONArray allUserData = unLockServletFormData.getALLData();
                    if (allUserData != null) {
                        response.getWriter().write(allUserData.toString());
                    } else {
                        response.getWriter().write("");
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
