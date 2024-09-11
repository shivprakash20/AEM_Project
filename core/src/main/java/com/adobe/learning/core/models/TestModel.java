package com.adobe.learning.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class TestModel {

    @Inject
    String articleType;

    @Inject
    String authorLimit;

    @Inject
    private boolean isSelectorValueSupport;

    @Inject
    private String title;


    @Inject
    private  String articleDescription;

    public String getArticleType() {
        return articleType;
    }

    public String getAuthorLimit() {
        String local = "Count";
        return local + authorLimit;
    }

    public boolean isSelectorValueSupport() {
        return isSelectorValueSupport;
    }

    public String getTitle() {
        return title;
    }

    public String getArticleDescription() {
        return articleDescription;
    }
}
