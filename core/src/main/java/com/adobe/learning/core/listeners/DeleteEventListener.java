package com.adobe.learning.core.listeners;

import org.apache.sling.api.SlingConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shiv
 */
@Component(service = EventHandler.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= Delete Events listener",
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/REMOVED",
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
        EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/CHANGED",
        EventConstants.EVENT_FILTER + "=(paths=/content/dam/*)"})

public class DeleteEventListener implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteEventListener.class);

    @Override
    public void handleEvent(Event event) {
        try {
            String eventPath = event.getProperty(SlingConstants.PROPERTY_PATH).toString();
            LOGGER.debug("Event Path {} : ", eventPath);
            
        } catch (Exception e) {
            LOGGER.error("Error in Replication Event : {}", e.getMessage());
        }
    }
}
