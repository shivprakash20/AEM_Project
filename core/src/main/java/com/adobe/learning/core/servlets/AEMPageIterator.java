package com.adobe.learning.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
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
import java.util.*;

/**
 * Servlet that take input as Number of Content.
 * http://localhost:4502/bin/aemPageIterator?pagePath=/content/learning/us/en
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Page Iterator",
        "sling.servlet.paths=" + "/bin/aemPageIterator", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class AEMPageIterator extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AEMPageIterator.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            // Get content fragment name from the request object
            String pagePath = request.getParameter("pagePath");

            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            Page rootPage = Objects.requireNonNull(resourceResolver.getResource(pagePath)).adaptTo(Page.class);
            assert rootPage != null;
            Iterator<Page> rootPageIterator = rootPage.listChildren(new PageFilter(false, false), false);
            List<String> list = new ArrayList<>();
            while (rootPageIterator.hasNext()) {
                Page childPage = rootPageIterator.next();
                list.add(childPage.getTitle());
            }
            response.getWriter().write("List of Pages : "+ list);

        } catch (LoginException | IOException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
