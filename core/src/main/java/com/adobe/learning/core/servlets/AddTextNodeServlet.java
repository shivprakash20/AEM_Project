package com.adobe.learning.core.servlets;

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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Add Text Node Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/addTextNode"
})
public class AddTextNodeServlet extends SlingSafeMethodsServlet {

    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);

            Node confNode = resourceResolver.getResource("/conf").adaptTo(Node.class);
            Node textNode = confNode.addNode("text.json", "nt:file");
            Node jcrContent = textNode.addNode("jcr:content", "nt:unstructured");
            session.save();
            response.getWriter().write("Text node added successfully.");
        } catch (RepositoryException | IOException | LoginException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
