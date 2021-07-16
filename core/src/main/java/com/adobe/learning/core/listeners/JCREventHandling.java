package com.adobe.learning.core.listeners;

import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.Objects;

@Component(service = EventListener.class, immediate = true)
public class JCREventHandling implements EventListener {

    private final Logger logger = LoggerFactory.getLogger(JCREventHandling.class);

    @Reference
    SlingRepository slingRepository;

    private Session session;

    @Activate
    protected void activate() {
        try {
            session = slingRepository.loginService("admin", null);
            logger.debug("Lister Session :");
            /* Here  null for default workspace and We can also take session form system user */

            // Adding the event listener
            Objects.requireNonNull(session).getWorkspace().getObservationManager().addEventListener(
                    this, Event.PROPERTY_ADDED | Event.NODE_ADDED, "/content/learning/us",
                    true, null, null, false);

        } catch (RepositoryException e) {
            logger.error(" Repository Exception : {}", e.getMessage());
        }
    }

    @Override
    public void onEvent(EventIterator events) {
        try {
            // Loop through all the events
            while (events.hasNext()) {
                // Get the current event
                Event event = events.nextEvent();
                logger.debug("Event was added by: {} at path: {}", event.getUserID(), event.getPath());
            }
        } catch (RepositoryException e) {
            logger.error("Exception occurred: {}: ", e.getMessage());
        }
    }
}
