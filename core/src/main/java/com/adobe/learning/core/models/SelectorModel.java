package com.adobe.learning.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelectorModel {


    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    private String selector;

    @PostConstruct
    protected void init() {
        selector = slingHttpServletRequest.getRequestPathInfo().getSelectorString();
    }

    public String getSelector() {
        return selector;
    }
}