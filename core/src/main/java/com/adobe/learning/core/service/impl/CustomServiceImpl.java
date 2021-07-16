package com.adobe.learning.core.service.impl;

import com.adobe.learning.core.service.CustomService;
import com.adobe.learning.core.service.config.AllOsgiConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = CustomService.class, immediate = true)
@Designate(ocd = AllOsgiConfig.class)
public class CustomServiceImpl implements CustomService {

    private final Logger logger = LoggerFactory.getLogger(CustomServiceImpl.class);

    AllOsgiConfig allOsgiConfig;

    @Activate
    protected void activate(AllOsgiConfig config){
        this.allOsgiConfig=config;
    }

    @Override
    public String getUser() {
        logger.debug("We are in Custom Service Impl");
        logger.debug("Configuration Enable :" + allOsgiConfig.enableConfig());
        return null;
    }
}
