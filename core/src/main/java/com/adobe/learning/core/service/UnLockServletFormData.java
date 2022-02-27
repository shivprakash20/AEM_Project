package com.adobe.learning.core.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface UnLockServletFormData {
    public JSONObject getUserData(int givenId);
    public JSONArray getALLData();
}
