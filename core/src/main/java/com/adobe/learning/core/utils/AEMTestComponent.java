package com.adobe.learning.core.utils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, name = AEMTestComponent.COMPONENT_NAME)
public class AEMTestComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AEMTestComponent.class);
    protected static final String COMPONENT_NAME = "Component Testing";

    @Activate
    protected void activate() {
        LOGGER.info("This is Basic Component :" +COMPONENT_NAME);
    }
}
