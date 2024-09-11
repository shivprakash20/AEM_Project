package com.adobe.learning.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet that take input as Content Fragment Name.
 * http://localhost:4502/bin/searchInContentFragment?searchText=shiv
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Search in content fragment description",
        "sling.servlet.paths=" + "/bin/searchInContentFragment", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SearchContentFragmentServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchContentFragmentServlet.class);

    private static final String SYSTEM_USER = "system_user";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder queryBuilder;

    Session session;

    List<String> arrayList;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        String fullSearchText = request.getParameter("searchText");

        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
            session = resourceResolver.adaptTo(Session.class);

            /* Using Query Builder To Read All author page inside the rootPath */
            HashMap<String, String> hashMap = new HashMap<>();
            // Add additional primary predicate
            hashMap.put("path", "/content/dam/learning");
            hashMap.put("type", "cq:ContentFragment");
            // Add the full-text predicate to the HashMap
            hashMap.put("fulltext", fullSearchText);
            // Add the orderby parameter to the HashMap
            hashMap.put("orderby", "@jcr:content/cq:lastModified");
            hashMap.put("orderby.sort", "desc");
            //Load all the result
            hashMap.put("p.limit", "-1");

            // Create the Query object from the HashMap
            Query query = queryBuilder.createQuery(PredicateGroup.create(hashMap), session);

            // Execute the query and get the results
            query.setStart(0);
            SearchResult result = query.getResult();
            List<Hit> hits = result.getHits();

            // Iterate through the results and do something with each hit
            arrayList = new ArrayList<>();
            for (Hit hit : hits) {
                String path = hit.getPath();
                arrayList.add(path);
            }
            //Writing response to the server
            response.getWriter().write(arrayList.toString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
