package com.adobe.learning.core.servlets;

import java.io.IOException;
import java.util.*;

import javax.jcr.Session;
import javax.servlet.Servlet;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/*
This servlet is for full text search on a specific paths
 */

@Component(service = Servlet.class, immediate = true, property = {"sling.servlet.paths=" + "/bin/aemSearchResults",
        "sling.servlet.methods=" + "GET"
})
public class SearchServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);

    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private QueryBuilder builder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        try {
            logger.info("Inside SearchServlet.doGet()");
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "sysUserWipro");
            ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param);
            String searchKeyword = request.getParameter("searchKeyword");
            String searchPath = request.getParameter("searchPath");
            String excludePath = request.getParameter("excludePath");
            logger.info("inside search servlet , searching results for key = {}", searchKeyword);

            Session session = resolver.adaptTo(Session.class);
            HashMap<String, String> map = new HashMap<>();

            ArrayList<String> nexusSearchPath = new ArrayList<>(Arrays.asList(searchPath.split(",")));
            ArrayList<String> nexusExcludePath = new ArrayList<>(Arrays.asList(excludePath.split(",")));
            ArrayList<String> finalSearchPath = new ArrayList<>();
            if (nexusSearchPath.contains("/content/nexus")) {
                Resource resource = request.getResource();
                PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
                assert pageManager != null;
                Page rootPage = pageManager.getPage("/content/nexus");
                Iterator<Page> rootPageIterator = rootPage.listChildren();
                while (rootPageIterator.hasNext()) {
                    finalSearchPath.add(rootPageIterator.next().getPath());
                }
            } else {
                finalSearchPath = nexusSearchPath;
            }

            /* Removing the Exclude Path */
            if (nexusExcludePath.size() > 0) {
                for (String localExcludePath : nexusExcludePath) {
                    finalSearchPath.remove(localExcludePath);
                }
            }

            int count = 1;
            map.put("fulltext", searchKeyword);
            map.put("type", "cq:Page");
            map.put("1_group.p.or", "true");
            for (String localPath : finalSearchPath) {
                map.put("1_group." + count + "_path", localPath);
                count++;
            }
            map.put("group.p.or", "true");
            map.put("group.1_property", "jcr:content/hideInSearch");
            map.put("group.1_property.operation", "exist");
            map.put("group.1_property.value", "false");
            map.put("group.2_property", "jcr:content/hideInSearch");
            map.put("group.2_property.operation", "not");
            map.put("p.limit", "50");

            Query query = builder.createQuery(PredicateGroup.create(map), session);
            query.setStart(0);
            SearchResult result = query.getResult();

            logger.info("Total number of results found : {}", result.getHits().size());

            response.getWriter().write("Total number of results found :" + result.getHits().size());

        } catch (LoginException e) {
            logger.error("Error in Search :{}", e.getMessage());
        }
    }
}
