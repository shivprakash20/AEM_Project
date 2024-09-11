package com.adobe.learning.core.models;

import com.adobe.learning.core.bean.TestBean;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Test {

    //Injecting the filePath authored in component dialog
    @Inject
    private String filePath;

    @SlingObject
    private ResourceResolver resourceResolver;

    List<TestBean> pageList;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        assert pageManager != null;
        Page rootPage = pageManager.getPage(filePath);
        //Fetching all child page
        Iterator<Page> rootPageIterator = rootPage.listChildren(new PageFilter(false, false), true);
        pageList = new ArrayList<>();
        //Getting path of all child pages
        while (rootPageIterator.hasNext()) {
            TestBean testBean = new TestBean();
            testBean.setTitle(rootPageIterator.next().getTitle());
            testBean.setPagePath(rootPageIterator.next().getPath());
            pageList.add(testBean);
        }
    }

    //Getting the list of pages
    public List<TestBean> getPageList() {
        return pageList;
    }
}