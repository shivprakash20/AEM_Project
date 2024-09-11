package com.adobe.learning.core.service.impl;

import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import java.util.Map;

public interface RestAPIService {

    /**
     * This method returns the response for the GET request to endpoints.
     *
     * @param request
     * @param response
     * @param formID
     * @return
     */
    JsonObject handleGETRequest(SlingHttpServletRequest request, SlingHttpServletResponse response, String formID,
                                Map<String, String> headers);
}
