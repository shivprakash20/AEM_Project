package com.adobe.learning.core.servlets;

import com.day.cq.wcm.commons.ReferenceSearch;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Shiv Prakash
 * http://localhost:8080/bin/referenceSearch?pagePath=/content/we-retail/us/en/bike
 */

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Reference Search",
        "sling.servlet.paths=" + "/bin/referenceSearch", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class ReferenceSearchServlet extends SlingAllMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceSearchServlet.class);

    ResourceResolver resourceResolver;

    ArrayList<String> pageList;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            pageList = new ArrayList<>();

            resourceResolver = request.getResourceResolver();
            //Asset or Page Path
            String pagePath = request.getParameter("pagePath");

            /* Reference Search of Page
             * Provide list of all pages where current page are being Used */
            ReferenceSearch referenceSearch = new ReferenceSearch();
            referenceSearch.setExact(true);
            referenceSearch.setHollow(true);
            referenceSearch.setMaxReferencesPerPage(-1);

            Collection<ReferenceSearch.Info> resultSet = referenceSearch.search(resourceResolver, pagePath).values();
            for (ReferenceSearch.Info info : resultSet) {
                String currentPage = info.getPagePath();
                pageList.add(currentPage);
            }

            /* Writing Internal Url */
            if (!pageList.isEmpty()) {
                response.getWriter().append("\nAll Page List-------------------------------------------- \n\n");
                pageList.forEach(relativePath -> {
                    try {
                        response.getWriter().append(relativePath).append("\n");
                    } catch (IOException e) {
                        logger.error("Error In Writing Local Url :{}", e.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            logger.error("Exception in Reference Search :{}", e.getMessage());
        }
    }
}
