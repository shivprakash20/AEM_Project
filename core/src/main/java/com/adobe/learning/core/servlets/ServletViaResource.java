package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;

@Component(
        service = Servlet.class, property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=" + "learning/components/page",
                "selectors=" + "test",
                "extensions=" + "json"
        }
)
public class ServletViaResource extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 7762806638577903486L;
    private static final String TAG = ServletViaResource.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletViaResource.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            // Getting the ResourceResolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();

            // Getting the session instance by adapting ResourceResolver
            Session session = resourceResolver.adaptTo(Session.class);

            response.getWriter().println("Hello India");

        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred: {}", TAG, e.getMessage());
        }
    }
}