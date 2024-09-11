package com.adobe.learning.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Servlet that take input as Number of Content.
 * http://localhost:4502/bin/pageRename?pagePath=/content/learning/us/en/changeme
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Page Rename",
        "sling.servlet.paths=" + "/bin/pageRename", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class AEMPageMove extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AEMPageMove.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            String pageName = request.getParameter("pagePath");

            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            assert pageManager != null;
            Page page = Objects.requireNonNull(resourceResolver.getResource(pageName)).adaptTo(Page.class);
            Page newPage = pageManager.move(page, "/content/learning/us/en/changed", null, false, true, null, null);

            response.getWriter().write("New Page Path : " + newPage.getPath());

        } catch (LoginException | IOException | WCMException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
