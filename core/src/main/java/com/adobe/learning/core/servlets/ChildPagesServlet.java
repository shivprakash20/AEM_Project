package com.adobe.learning.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.Iterator;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Child Pages",
        "sling.servlet.paths=" + "/bin/childpageservlet", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class ChildPagesServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChildPagesServlet.class);
    private static final String ROOT_PATH = "/content/learning";
    private static final String EXTERNAL_PATH = "externalpath";
    private static final String CATEGORY_NAME = "categoryname";
    private static final String NAVIGATION_TITLE = "navigationtitle";
    private static final String ENABLE_WARRANTY = "enablewarranty";
    private static final int DEPTH_LEVEL = 2;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        try {

            ResourceResolver resourceResolver = request.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            assert pageManager != null;
            Page rootPage = pageManager.getPage(ROOT_PATH);

            JSONArray pagesJsonArray = getChildrenPagesJson(rootPage, DEPTH_LEVEL);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.valueOf(pagesJsonArray));

        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }


    }


    /**
     * This method iterate the child pages and update JSON.
     * @param page is root page.
     * @param level is for page depth.
     * @return the page JSONArray.
     * @throws JSONException for JSON Exception.
     */
    private JSONArray getChildrenPagesJson(Page page, int level) throws JSONException {
        //JSON Array for child pages
        JSONArray childrenJsonArray = new JSONArray();
        if (level > 0 && hasChildPages(page)) {
            Iterator<Page> childPages = page.listChildren();
            while (childPages.hasNext()) {
                Page childPage = childPages.next();
                JSONObject childPageJson = new JSONObject();
                if (updateJsonWithChildPage(childPageJson, childPage)) {
                    childPageJson.put("children", getChildrenPagesJson(childPage, level - 1));
                    childrenJsonArray.put(childPageJson);
                }
            }
        }
        return childrenJsonArray;
    }


    /**
     * This method is used to check child pages.
     *
     * @param page is page object.
     * @return true for have child pages.
     */
    private boolean hasChildPages(Page page) {
        if (page != null) {
            // Use listChildren to get an iterator over the child pages
            Iterator<Page> children = page.listChildren();
            // Returns true if there are child pages
            return children.hasNext();
        }
        // Returns false if the page does not exist or has no children
        return false;
    }

    /**
     * This method is to update pages properties.
     *
     * @param jsonObjects store the page details.
     * @param page        is page Object.
     * @return true for non warranty pages.
     * @throws JSONException for any JSON Exception.
     */
    public boolean updateJsonWithChildPage(JSONObject jsonObjects, Page page) throws JSONException {

        boolean noWarrantyStatus = true;
        //Getting all properties in ValueMap
        ValueMap pageProperties = page.getProperties();

        jsonObjects.put("title", page.getTitle());
        jsonObjects.put("path", page.getPath());

        if (pageProperties.containsKey(EXTERNAL_PATH)) {
            jsonObjects.put(EXTERNAL_PATH, pageProperties.get(EXTERNAL_PATH, String.class));
        }
        if (pageProperties.containsKey(CATEGORY_NAME)) {
            jsonObjects.put(CATEGORY_NAME, pageProperties.get(CATEGORY_NAME, String.class));
        }
        if (pageProperties.containsKey(NAVIGATION_TITLE)) {
            jsonObjects.put(NAVIGATION_TITLE, pageProperties.get(NAVIGATION_TITLE, String.class));
        }
        if (pageProperties.containsKey(ENABLE_WARRANTY)
                && "true".equalsIgnoreCase(pageProperties.get(ENABLE_WARRANTY, String.class))) {
            noWarrantyStatus = false;
        }

        return noWarrantyStatus;
    }
}
