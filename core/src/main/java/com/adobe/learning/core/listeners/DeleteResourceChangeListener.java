package com.adobe.learning.core.listeners;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Shiv
 */
@Component(service = ResourceChangeListener.class, immediate = true, property = {
        ResourceChangeListener.CHANGES + "=ADDED",
        ResourceChangeListener.CHANGES + "=CHANGED",
        ResourceChangeListener.CHANGES + "=REMOVED",
        ResourceChangeListener.PATHS + "=/content/learning/*"})

public class DeleteResourceChangeListener implements ResourceChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteResourceChangeListener.class);

    @Override
    public void onChange(@NotNull List<ResourceChange> list) {
        try {
            ResourceChange resourceChange = list.get(0);
            LOGGER.debug("Event Path {} : ", resourceChange.getPath());

        } catch (Exception e) {
            LOGGER.error("Error in Deletion Event : {}", e.getMessage());
        }
    }
}
