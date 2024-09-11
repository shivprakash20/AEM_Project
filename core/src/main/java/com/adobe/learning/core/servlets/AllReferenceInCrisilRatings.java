package com.adobe.learning.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Shiv Prakash
 * http://localhost:4502/bin/bin/crisil-ratings?rootPath=/content/crisilratings
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Page asset in given path",
        "sling.servlet.paths=" + "/bin/crisil-ratings", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class AllReferenceInCrisilRatings extends SlingAllMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(AllReferenceInCrisilRatings.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder queryBuilder;

    Session session;

    ArrayList<String> pageList;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        pageList = new ArrayList<>();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
            session = resourceResolver.adaptTo(Session.class);

            /* Reading all URL Parameter*/
            String parentPath = request.getParameter("rootPath");

            /* Start Value Update for individual page*/
            String queryPath = parentPath + "/" + JcrConstants.JCR_CONTENT;

            /* Updating all Properties  on Root Node */
            crisilPageList(queryPath, resourceResolver, pageList);

            /* Using Query Builder To Read All author page inside the rootPath */
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("path", queryPath);
            hashMap.put("group.p.or", "true");
            hashMap.put("group.1_property", "@jcr:primaryType");
            hashMap.put("group.1_property.value", "cq:PageContent");
            hashMap.put("group.2_property", "@jcr:primaryType");
            hashMap.put("group.2_property.value", "nt:unstructured");
            hashMap.put("p.limit", "-1");

            Query query = queryBuilder.createQuery(PredicateGroup.create(hashMap), session);
            query.setStart(0);
            SearchResult result = query.getResult();

            /* Updating all Properties  on Children Node */
            for (Hit hit : result.getHits()) {
                String currentPath = hit.getPath();
                crisilPageList(currentPath, resourceResolver, pageList);
            }
            /* End Value Update for individual page*/

            /* Start Value Update for individual child page*/
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            assert pageManager != null;
            Page rootPage = pageManager.getPage(parentPath);
            Iterator<Page> rootPageIterator = rootPage.listChildren(new PageFilter(), true);

            while (rootPageIterator.hasNext()) {
                Page childPage = rootPageIterator.next();
                String childPath = childPage.getPath();

                /* Start Value Update for individual child page*/
                String childQueryPath = childPath + "/" + JcrConstants.JCR_CONTENT;

                /* Updating all Properties  on Root Node */
                crisilPageList(childQueryPath, resourceResolver, pageList);

                /* Using Query Builder To Read All author page inside the childPath */
                HashMap<String, String> childHashMap = new HashMap<>();
                childHashMap.put("path", childQueryPath);
                childHashMap.put("group.p.or", "true");
                childHashMap.put("group.1_property", "@jcr:primaryType");
                childHashMap.put("group.1_property.value", "cq:PageContent");
                childHashMap.put("group.2_property", "@jcr:primaryType");
                childHashMap.put("group.2_property.value", "nt:unstructured");
                childHashMap.put("p.limit", "-1");

                Query childQuery = queryBuilder.createQuery(PredicateGroup.create(childHashMap), session);
                childQuery.setStart(0);
                SearchResult childResult = childQuery.getResult();

                /* Updating all Properties  on Children Node */
                for (Hit childHit : childResult.getHits()) {
                    String childCurrentPath = childHit.getPath();
                    crisilPageList(childCurrentPath, resourceResolver, pageList);
                }
                /* End Value Update for individual child page*/

            }

            assert session != null;
            session.save();
            session.logout();

            /* Writing ALL URL and Response Code on Response */
            response.getWriter().append("List of All Crisil Pages \n\n");

            /* Writing Internal Url */
            if (!pageList.isEmpty()) {
                response.getWriter().append("All Reference Page List-------------------------------------------- \n\n");
                pageList.forEach(relativePath -> {
                    try {
                        response.getWriter().append(relativePath).append("\n");
                    } catch (IOException e) {
                        logger.error("Error In Writing Local Url " + e);
                    }
                });
            }

        } catch (LoginException | RepositoryException | IOException | JSONException e) {
            logger.error("Exception in updating properties :", e);
        }
    }

    /* Methods to update page properties*/
    void crisilPageList(String currentPath, ResourceResolver resourceResolver, ArrayList<String> pageList) throws RepositoryException, JSONException {

        Resource currentResource = resourceResolver.getResource(currentPath);
        assert currentResource != null;
        Node currentNode = currentResource.adaptTo(Node.class);
        assert currentNode != null;
        PropertyIterator currentIterator = currentNode.getProperties();

        while (currentIterator.hasNext()) {
            Property currentProperty = currentIterator.nextProperty();

            if (currentProperty.getDefinition().isMultiple()) {
                Value[] propValues = currentProperty.getValues();
                for (Value localItem : propValues) {
                    String localValue = localItem.getString();
                    if (isValidJSON(localValue)) {
                        JSONObject jsonObject = new JSONObject(localValue);
                        Iterator keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String localKey = (String) keys.next();
                            String jsonVal = jsonObject.optString(localKey, "default");
                            if (jsonVal.startsWith("/content/crisil/")) {
                                pageList.add(jsonVal);
                            }
                        }
                    } else {
                        if (localValue.startsWith("/content/crisil/")) {
                            pageList.add(localValue);
                        }
                    }
                }
            } else {
                String propValue = currentProperty.getValue().getString();
                if (isValidJSON(propValue)) {
                    JSONObject jsonObject = new JSONObject(propValue);
                    Iterator keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String localKey = (String) keys.next();
                        String jsonVal = jsonObject.optString(localKey, "default");
                        if (jsonVal.startsWith("/content/crisil/")) {
                            pageList.add(jsonVal);
                        }
                    }
                } else {
                    if (propValue.startsWith("/content/crisil/")) {
                        pageList.add(propValue);
                    }
                }
            }
        }
    }

    private boolean isValidJSON(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }
}