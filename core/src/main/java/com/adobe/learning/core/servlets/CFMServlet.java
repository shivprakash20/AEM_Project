package com.adobe.learning.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.Iterator;

/**
 * Servlet that take input as Content Fragment Name.
 * http://localhost:4502/bin/contentFragmentModel?cfName=cfm-test
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Content Fragment Model Description",
        "sling.servlet.paths=" + "/bin/contentFragmentModel", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class CFMServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CFMServlet.class);
    private static final String CF_PATH = "/content/dam/learning/";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            String cfName = request.getParameter("cfName");

            JsonObject jsonObject = new JsonObject();

            // Getting the ResourceResolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource resource = resourceResolver.getResource(CF_PATH + cfName);
            assert resource != null;
            ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);
            assert contentFragment != null;
            Iterator<ContentElement> contentElements = contentFragment.getElements();
            while (contentElements.hasNext()) {
                ContentElement contentElement = contentElements.next();
                jsonObject.addProperty(contentElement.getName(), contentElement.getContent());
            }

            String jsonData = jsonObject.toString();

            response.getWriter().write(jsonData);

        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
