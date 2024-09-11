package com.adobe.learning.core.models;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TestModelTest {

    @Mock
    private Resource resource;

    private TestModel testModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        testModel = new TestModel();
    }

    @Test
    void testGetArticleType() {
        when(resource.adaptTo(TestModel.class)).thenReturn(testModel);

        //setting up the context
        testModel.articleType = "Bharat";

        //expected value
        String expectedValue = "Bharat";
        assertEquals(expectedValue, testModel.getArticleType());
    }

    @Test
    void testGetAuthorLimit() {
        when(resource.adaptTo(TestModel.class)).thenReturn(testModel);

        //setting up the context
        testModel.authorLimit = "5";

        //expected value
        String expectedValue = "Count5";
        assertEquals(expectedValue, testModel.getAuthorLimit());
    }
}
