package com.adobe.learning.core.servlets;

import com.day.cq.replication.ReplicationStatus;
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
 * http://localhost:4502/bin/aemPageCreation?pageName=samplePage
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Page Programmatically Description",
        "sling.servlet.paths=" + "/bin/aemPageCreation", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class AEMPageProgrammatically extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AEMPageProgrammatically.class);
    private static final String SYSTEM_USER = "system_user";
    private static final String CONTENT_PATH = "/content/learning/us/en";
    private static final String TEMPLATE = "/conf/learning/settings/wcm/templates/page-content";
    private static final String RENDERER = "learning/components/page";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            String pageName = request.getParameter("pageName");

            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            assert pageManager != null;
            Page page = pageManager.create(CONTENT_PATH, pageName, TEMPLATE, pageName);

            Node pageNode = page.adaptTo(Node.class);

            Node jcrNode = null;

            if (page.hasContent()) {
                jcrNode = page.getContentResource().adaptTo(Node.class);
            } else {
                assert pageNode != null;
                jcrNode = pageNode.addNode("jcr:content", "cqPageContent");
            }

            assert jcrNode != null;
            jcrNode.setProperty("sling:resourceType", RENDERER);

            jcrNode.getSession().save();

            assert session != null;
            session.save();

            response.getWriter().write("Page created successfully");

        } catch (LoginException | IOException | WCMException | RepositoryException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
