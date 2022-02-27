package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
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
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Servlet that take input as Number of Content.
 * http://localhost:4502/bin/contentCreation?cnNumber=10000
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Content Creation Description",
        "sling.servlet.paths=" + "/bin/contentCreation", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class ContentCreationServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentCreationServlet.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            int cfName = Integer.parseInt(request.getParameter("cnNumber"));

            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);
            Resource resource = resourceResolver.getResource("/content");
            assert resource != null;
            Node node = resource.adaptTo(Node.class);

            assert node != null;
            if (!node.hasNode("sample-data")) {
                Node sampleData = node.addNode("sample-data", "sling:Folder");
                sampleData.setProperty("jcr:title", "Sample Data");
                sampleData.getSession().save();
            }
            Node sampleDataNode = node.getNode("sample-data");
            //Fetch Data on node.getProperty("jcr:title").getString();
            IntStream.range(0, cfName).forEach(index -> {
                try {
                Random random = new Random(System.currentTimeMillis());
                int number = random.nextInt(100000);
                String formattedNumber = "sample"+String.format("%05d", number);
                String sampleId = "11111"+String.format("%05d", number);
                Node currentChildNode = sampleDataNode.addNode(formattedNumber, "nt:unstructured");
                currentChildNode.setProperty("name",formattedNumber);
                currentChildNode.setProperty("id",sampleId);
                currentChildNode.getSession().save();
                } catch (RepositoryException e) {
                    LOGGER.error("Exception occurred: {}", e.getMessage());
                }
            });
            assert session != null;
            session.save();
            response.getWriter().write("Node are created successfully");

        } catch (RepositoryException | LoginException | IOException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
