package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=White Level Servlet Description",
        "sling.servlet.paths=" + "/bin/whiteLevelServlet", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class WhiteLevelServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(WhiteLevelServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {

            // Getting the ResourceResolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);

            LOGGER.info("Session info : {}", session);
            response.getWriter().write(String.valueOf(session));

        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
