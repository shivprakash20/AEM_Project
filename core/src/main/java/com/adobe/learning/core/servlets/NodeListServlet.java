package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.query.*;
import javax.servlet.Servlet;
import java.util.Objects;

/*
http://localhost:4502/bin/learning/page?nodePath=/content/learning&nodeType=cq:Page
 */

@Component(service = Servlet.class, property = {"sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/learning/page"
})
public class NodeListServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 7762806638577908286L;
    private static final String TAG = NodeListServlet.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeListServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        // JCR Session
        LOGGER.debug("Servlet Started :-");
        Session session = null;
        try {
            // Get path from the request object
            String nodePath = request.getParameter("nodePath");

            // Get node type
            String nodeType = request.getParameter("nodeType");

            // Getting the ResourceResolver from the current request
            ResourceResolver resourceResolver = request.getResourceResolver();

            // Getting the session instance by adapting ResourceResolver
            session = resourceResolver.adaptTo(Session.class);

            // Get the instance of QueryManager from the JCR workspace
            QueryManager queryManager = Objects.requireNonNull(session).getWorkspace().getQueryManager();

            // This query will look for all the assets under the given path
            String queryString = "SELECT * FROM [" + nodeType + "] WHERE ISDESCENDANTNODE('" + nodePath + "')";

            // Converting the String query into an executable query object
            Query query = queryManager.createQuery(queryString, "JCR-SQL2");

            // Executing the query
            QueryResult queryResult = query.execute();

            // This will behave as a cursor pointing to the current row of results
            RowIterator rowIterator = queryResult.getRows();

            JSONObject jsonObject = new JSONObject();
            int count = 0;
            // Loop for all the rows in the result and return them as json
            while (rowIterator.hasNext()) {
                Row row = rowIterator.nextRow();
                PropertyIterator propertyIterator = row.getNode().getProperties();
                JSONObject properties = new JSONObject();
                while (propertyIterator.hasNext()) {
                    Property property = propertyIterator.nextProperty();
                    properties.put(property.getName(), property.getValue());
                }
                jsonObject.put("page-" + (++count), properties);
            }
            // Printing the response to the browser window
            response.getWriter().println(jsonObject.toString());
        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred: {}", TAG, e.getMessage());
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
