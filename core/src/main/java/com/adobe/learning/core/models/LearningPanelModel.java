package com.adobe.learning.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Random;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LearningPanelModel {

    @Inject
    private String firstName;

    @Inject
    private String lastName;

    @Inject
    private String gender;

    @Inject
    private String dob;

    @Inject
    private String address;

    @Inject
    private String isHome;

    private int uniqueId;

    @PostConstruct
    public void init() {
        Random random = new Random(System.currentTimeMillis());
        uniqueId = 10000 + random.nextInt(20000);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getIsHome() {
        return isHome;
    }

    public int getUniqueId() {
        return uniqueId;
    }
}
