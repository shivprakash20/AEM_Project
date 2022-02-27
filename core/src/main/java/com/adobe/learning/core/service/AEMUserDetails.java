package com.adobe.learning.core.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface AEMUserDetails {

    public JSONObject getAllUserDetails(String userName);
    public JSONArray getAllGroupDetails(String groupName);
}
