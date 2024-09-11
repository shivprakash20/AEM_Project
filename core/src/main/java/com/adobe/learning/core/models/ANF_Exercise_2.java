
package com.adobe.learning.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ANF_Exercise_2 {

    List<ANFBean> list = new ArrayList<>();

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {

        Iterator<Resource> resource = Objects.requireNonNull(resourceResolver.getResource("/var/commerce/products/anf-code-challenge/newsData")).listChildren();

        while (resource.hasNext()) {
            ValueMap valueMap = resource.next().getValueMap();
            ANFBean anfBean = new ANFBean();
            anfBean.setAuthor(valueMap.get("author", String.class));
            list.add(anfBean);
        }
    }

    public List<ANFBean> getList() {
        return list;
    }
}