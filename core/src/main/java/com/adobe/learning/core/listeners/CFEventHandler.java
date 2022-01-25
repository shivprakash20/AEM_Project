package com.adobe.learning.core.listeners;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true, service = EventHandler.class, property = {
        Constants.SERVICE_DESCRIPTION + "= This event handler listens the events on content fragment creation",
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/CHANGED",
        EventConstants.EVENT_FILTER + "(&" + "(path=/content/dam/*/jcr:content) (&("
                + ResourceChangeListener.CHANGES + "=contentFragment)))" })
public class CFEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFEventHandler.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    public void handleEvent(Event event) {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            Session session = resourceResolver.adaptTo(Session.class);
            String eventPath = event.getProperty(SlingConstants.PROPERTY_PATH).toString();
            String cfPath = eventPath.substring(0, eventPath.indexOf("/jcr:content"));

            Random random = new Random(System.currentTimeMillis());
            int number = random.nextInt(100000);
            String formattedNumber = String.format("%05d", number);

            Resource resource = resourceResolver.getResource(cfPath);
            assert resource != null;
            ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);
            assert contentFragment != null;
            Iterator<ContentElement> contentElements = contentFragment.getElements();
            while (contentElements.hasNext()) {
                ContentElement contentElement = contentElements.next();
                if("cfKey".equals(contentElement.getName())){
                    contentElement.setContent(formattedNumber,"text/plain");
                    break;
                }
            }
            if(session !=null)
                session.save();
            LOGGER.info("Event Path : {}", event.getProperty(SlingConstants.PROPERTY_PATH));
        } catch (LoginException | RepositoryException | ContentFragmentException e) {
            e.printStackTrace();
        }


    }
}
