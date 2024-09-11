package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

@Component(service = Filter.class)
public class AssetFilterServlet implements Filter {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetFilterServlet.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        String resourceType = slingRequest.getResource().getResourceType();
        String path = slingRequest.getRequestPathInfo().getResourcePath();

        if ("dam:Asset".equals(resourceType) && path.startsWith("/content/dam/")) {
            slingResponse.sendError(SlingHttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        // allow the request to continue

    }
}
