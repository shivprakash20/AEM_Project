package com.adobe.learning.core.service.impl;

import com.adobe.learning.core.service.AEMUserAndGroupDetails;
import com.adobe.learning.core.service.ResourceResolverService;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.Iterator;

/**
 * Service that take input as UserName or GroupName and return the Email Address
 */
@Component(service = AEMUserAndGroupDetails.class, immediate = true)
public class AEMUserAndGroupDetailsImpl implements AEMUserAndGroupDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(AEMUserAndGroupDetailsImpl.class);
    private static final String USER_EMAIL = "profile/email";
    private static final String USER_FNAME = "profile/givenName";
    private static final String USER_LNAME = "profile/familyName";

    @Reference
    ResourceResolverService resourceResolverService;

    /**
     * @param userName give input as userName.
     * @return userDetails in Json Format.
     */
    @Override
    public JSONObject getAllUserDetails(String userName) {
        JSONObject userDetails = new JSONObject();
        try (ResourceResolver resourceResolver = resourceResolverService.getResourceResolver()) {
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            assert userManager != null;
            Authorizable authorizable = userManager.getAuthorizable(userName);

            if (authorizable != null && !authorizable.isGroup()) {
                userDetails = getUserDetails(authorizable);
            }

        } catch (LoginException | RepositoryException | JSONException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
        return userDetails;
    }

    @Override
    public JSONArray getAllGroupDetails(String groupName) {
        JSONArray groupDetails = new JSONArray();
        try (ResourceResolver resourceResolver = resourceResolverService.getResourceResolver()) {
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            assert userManager != null;
            Authorizable authorizable = userManager.getAuthorizable(groupName);
            if (authorizable != null && authorizable.isGroup()) {
                Group group = (Group) userManager.getAuthorizable(groupName);
                assert group != null;
                Iterator<Authorizable> allAuthorizeMembers = group.getMembers();
                while (allAuthorizeMembers.hasNext()) {
                    JSONObject currentUserDetails = getUserDetails(allAuthorizeMembers.next());
                    groupDetails.put(currentUserDetails);
                }
            }

        } catch (LoginException | RepositoryException | JSONException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
        return groupDetails;
    }

    /**
     * @param authorizable Take Authorizable Object as Input.
     * @return Json Object as User Details.
     * @throws RepositoryException Throw RepositoryException.
     * @throws JSONException       Throw JSONException.
     */
    private static JSONObject getUserDetails(Authorizable authorizable) throws RepositoryException, JSONException {
        JSONObject jsonObject = new JSONObject();
        if (authorizable.hasProperty(USER_FNAME)) {
            Value[] fNameVal = authorizable.getProperty(USER_FNAME);
            if (fNameVal != null)
                jsonObject.put("First Name", fNameVal[0].getString());
        }
        if (authorizable.hasProperty(USER_LNAME)) {
            Value[] lNameVal = authorizable.getProperty(USER_LNAME);
            if (lNameVal != null)
                jsonObject.put("Last Name", lNameVal[0].getString());
        }
        if (authorizable.hasProperty(USER_EMAIL)) {
            Value[] emailVal = authorizable.getProperty(USER_EMAIL);
            if (emailVal != null)
                jsonObject.put("Email", emailVal[0].getString());
        }

        return jsonObject;
    }

}
