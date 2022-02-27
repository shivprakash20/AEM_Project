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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet that take input as Number of Content.
 * http://localhost:4502/bin/aemPageCreate?pageName=samplePage
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Page Programmatically Description",
        "sling.servlet.paths=" + "/bin/aemPageCreate", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class AEMPageCreatedByUser extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AEMPageCreatedByUser.class);
    private static final String CONTENT_PATH = "/content/learning/us/en";
    private static final String TEMPLATE = "/conf/learning/settings/wcm/templates/page-content";
    private static final String RENDERER = "learning/components/page";

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            String pageName = request.getParameter("pageName");
            resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            assert pageManager != null;
            Page page = pageManager.create(CONTENT_PATH, pageName, TEMPLATE, pageName);
            Node pageNode = page.adaptTo(Node.class);

            Node jcrContentNode = null;
            if (page.hasContent()) {
                jcrContentNode = page.getContentResource().adaptTo(Node.class);
            } else {
                assert pageNode != null;
                jcrContentNode = pageNode.addNode("jcr:content", "cqPageContent");
            }

            assert jcrContentNode != null;
            jcrContentNode.setProperty("sling:resourceType", RENDERER);
            jcrContentNode.getSession().save();

            assert session != null;
            session.save();

            response.getWriter().write("Page created successfully");

        } catch (IOException | WCMException | RepositoryException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
