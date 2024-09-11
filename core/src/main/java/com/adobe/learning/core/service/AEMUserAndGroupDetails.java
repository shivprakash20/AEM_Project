package com.adobe.learning.core.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface AEMUserAndGroupDetails {

    public JSONObject getAllUserDetails(String userName);
    public JSONArray getAllGroupDetails(String groupName);

}
