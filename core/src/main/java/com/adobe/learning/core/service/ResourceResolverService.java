package com.adobe.learning.core.service;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverService {
    public ResourceResolver getResourceResolver() throws LoginException;
}
