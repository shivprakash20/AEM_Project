package com.adobe.learning.core.service.impl;

import com.adobe.learning.core.service.ResourceResolverService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;

@Component(service = ResourceResolverService.class, immediate = true)
public class ResourceResolverServiceImpl implements ResourceResolverService {

    private static final String SYSTEM_USER = "system_user";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getResourceResolver() throws LoginException {

        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
        return resourceResolverFactory.getServiceResourceResolver(param);
    }
}
