package com.adobe.learning.core.workflow;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
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
import java.util.Objects;

/**
 * @author Shiv
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Drop Down",
        "sling.servlet.paths=" + "/bin/packageDropdown", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class PackageDropdownServlet extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(PackageDropdownServlet.class);

    transient ResourceResolver resourceResolver;
    transient Resource pathResource;
    transient ValueMap valueMap;
    transient List<Resource> resourceList;

    @Reference
    transient QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            resourceResolver = request.getResourceResolver();
            pathResource = request.getResource();
            resourceList = new ArrayList<>();
            Session session = request.getResourceResolver().adaptTo(Session.class);

            /* AEM Package Path */
            String packagePath = Objects.requireNonNull(pathResource.getChild("datasource")).getValueMap().get("packagePath", String.class);
            assert packagePath != null;

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("path", packagePath);
            hashMap.put("path.flat", "true"); //For getting package available at same node level
            hashMap.put("type", "nt:file");
            hashMap.put("orderby", "@jcr:created");
            hashMap.put("orderby.sort", "desc");
            hashMap.put("p.limit", "-1");

            Query query = queryBuilder.createQuery(PredicateGroup.create(hashMap), session);
            query.setStart(0);
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                valueMap = new ValueMapDecorator(new HashMap<>());
                Resource packageResource = hit.getResource();
                valueMap.put("value", packageResource.getPath());
                valueMap.put("text", packageResource.getName());
                resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", valueMap));
            }

            /*Create a DataSource that is used to populate the drop-down control*/
            DataSource dataSource = new SimpleDataSource(resourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (Exception e) {
            logger.error("Error in Getting Drop Down Values {}", e.getMessage());
        }
    }
}
