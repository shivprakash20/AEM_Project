package com.adobe.learning.core.servlets;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Tags Search",
        "sling.servlet.paths=" + "/bin/tagsFind", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class PageSearchOrderWithTags extends SlingAllMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceSearchServlet.class);
    ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            resourceResolver = request.getResourceResolver();

            //Tags absolute path as Tag ID
            String[] allTags = {"we-retail:activity/biking", "we-retail:activity/hiking", "we-retail:activity/running"};

            //agManager instance
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

            //Range Iterator
            RangeIterator<Resource> resourceRangeIterator = tagManager.find("/content/learning", allTags, true);

            while (resourceRangeIterator.hasNext()) {
                Resource result = resourceRangeIterator.next();
                String path = result.getPath();
                //Custom Code Implementation
            }
        } catch (Exception e) {
            logger.error("Exception in Tags Search :{}", e.getMessage());
        }
    }
}
