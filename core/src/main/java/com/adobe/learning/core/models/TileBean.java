package com.adobe.learning.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TileBean {

    @Inject
    private String tileTitle;

    @Inject
    private String tileDesc;

    public String getTileTitle() {
        return tileTitle;
    }

    public String getTileDesc() {
        return tileDesc;
    }
}
