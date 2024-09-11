package com.adobe.learning.core.listeners;

import java.util.List;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ResourceChangeListener.class, property = {
        //Filter the notifications by path in the repo. Can be Arrayed and supports globs
        ResourceChangeListener.PATHS + "=" + "/content",
        //The type of change you want to listen too.
        ResourceChangeListener.CHANGES + "=" + "ADDED",
        ResourceChangeListener.CHANGES + "=" + "REMOVED",
        ResourceChangeListener.CHANGES + "=" + "CHANGED"
})
public class SampleResourceChangeListener implements ResourceChangeListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(SampleResourceChangeListener.class);

    @Override
    public void onChange(List<ResourceChange> list) {
        list.forEach(change -> {
            LOGGER.info(change.getPath());
            //Custom Code Implementations
        });
    }
}
