package com.adobe.learning.core.servlets;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.Servlet;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shiv
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= User and Group creation and permission",
        "sling.servlet.paths=" + "/bin/userGroupCreation", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class UserGroupCreationPermission extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserGroupCreationPermission.class);
    private static final String SYSTEM_USER = "system_user";

    @Reference
    transient ResourceResolverFactory resourceResolverFactory;

    transient ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        //Fetching all User Details from Servlet Parameter
        String userID = request.getParameter("userID");
        String userFirstName = request.getParameter("userFirstName");
        String userLastName = request.getParameter("userLastName");
        String userEmail = request.getParameter("userEmail");
        String password = request.getParameter("password");

        //Fetching all Group Details from Servlet Parameter
        String groupID = request.getParameter("groupID");
        String groupName = request.getParameter("groupName");
        String groupEmail = request.getParameter("groupEmail");

        try {
            //Getting ResourceResolver and Session using System Users
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);
            assert session != null;

            //Getting UserManager from ResourceResolver
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            assert userManager != null;

            //Creation of a new user with userID
            User createdUser = null;
            if (userManager.getAuthorizable(userID) == null) {
                createdUser = userManager.createUser(userID, password);

                //Setting the createdUser Profile Property
                ValueFactory valueFactory = session.getValueFactory();
                Value firstNameValue = valueFactory.createValue(userFirstName, PropertyType.STRING);
                createdUser.setProperty("./profile/givenName", firstNameValue);

                Value lastNameValue = valueFactory.createValue(userLastName, PropertyType.STRING);
                createdUser.setProperty("./profile/familyName", lastNameValue);

                Value emailValue = valueFactory.createValue(userEmail, PropertyType.STRING);
                createdUser.setProperty("./profile/email", emailValue);

                session.save();
                logger.info("User successfully created with ID : {}", createdUser.getID());
            } else {
                logger.info("User already exist..");
            }


            //Creation of a new group with groupID
            Group createdGroup = null;
            if (userManager.getAuthorizable(groupID) == null) {
                createdGroup = userManager.createGroup(groupID);

                //Setting the createdGroup Profile Property
                ValueFactory valueFactory = session.getValueFactory();
                Value groupNameValue = valueFactory.createValue(groupName, PropertyType.STRING);
                createdGroup.setProperty("./profile/givenName", groupNameValue);

                Value groupEmailValue = valueFactory.createValue(groupEmail, PropertyType.STRING);
                createdGroup.setProperty("./profile/email", groupEmailValue);

                session.save();
                logger.info("Group successfully created with ID : {}", createdGroup.getID());
            } else {
                logger.info("Group already exist..");
            }

            //Adding the User to the Group
            if (createdUser != null && createdGroup != null) {
                Authorizable authorizeUser = userManager.getAuthorizable(createdUser.getID());
                createdGroup.addMember(authorizeUser);
            }

            //Getting AccessControlManager from session
            AccessControlManager accessControlManager = session.getAccessControlManager();

            //Adding an ACL Permission to a User for a Specific Path (/conf/learning)
            JackrabbitAccessControlList specificPathUserAcl = AccessControlUtils.getAccessControlList(session, "/conf/learning");
            if (specificPathUserAcl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(userID);
                //Providing all permission for /conf/learning
                Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_ALL);

                specificPathUserAcl.addEntry(principal, privileges, true);
                accessControlManager.setPolicy(specificPathUserAcl.getPath(), specificPathUserAcl);
                session.save();
            } else
                logger.info("JackrabbitAccessControlList is null for specific path (/conf/learning).");

            //Adding an ACL Permission to a User for All Path (/)
            JackrabbitAccessControlList allPathUserAcl = AccessControlUtils.getAccessControlList(session, "/");
            if (allPathUserAcl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(userID);
                Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_READ, Privilege.JCR_READ_ACCESS_CONTROL);

                allPathUserAcl.addEntry(principal, privileges, true);
                accessControlManager.setPolicy(allPathUserAcl.getPath(), allPathUserAcl);
                session.save();
            } else
                logger.info("JackrabbitAccessControlList is null for the All JCR path (/).");

            //Removing ACL for a given Path (setting rep:policy -> deny)
            JackrabbitAccessControlList removeUserAcl = AccessControlUtils.getAccessControlList(session, "/conf/we-retail");
            if (removeUserAcl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(userID);
                Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_ALL);

                removeUserAcl.addEntry(principal, privileges, false);
                accessControlManager.setPolicy(removeUserAcl.getPath(), removeUserAcl);
                session.save();
            } else
                logger.info("JackrabbitAccessControlList is null for given path");

            //Adding an ACL Permission to a Group for a Specific Path (/conf/we-retail)
            JackrabbitAccessControlList specificPathGroupAcl = AccessControlUtils.getAccessControlList(session, "/conf/we-retail");
            if (specificPathGroupAcl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(groupID);
                Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_READ, Privilege.JCR_READ_ACCESS_CONTROL);

                specificPathGroupAcl.addEntry(principal, privileges, true);
                accessControlManager.setPolicy(specificPathGroupAcl.getPath(), specificPathGroupAcl);
                session.save();
            } else
                logger.info("JackrabbitAccessControlList is null for specific path (/conf/we-retail).");

            //Adding an ACL Permission to a Group for All Path (/)
            JackrabbitAccessControlList allPathGroupAcl = AccessControlUtils.getAccessControlList(session, "/");
            if (allPathGroupAcl != null) {
                JackrabbitSession jackrabbitSession = (JackrabbitSession) session;
                PrincipalManager principalManager = jackrabbitSession.getPrincipalManager();
                Principal principal = principalManager.getPrincipal(groupID);
                Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, Privilege.JCR_WRITE);

                allPathGroupAcl.addEntry(principal, privileges, true);
                accessControlManager.setPolicy(allPathGroupAcl.getPath(), allPathGroupAcl);
                session.save();
            } else
                logger.info("JackrabbitAccessControlList is null for the All JCR path (/).");

            session.logout();
        } catch (RepositoryException | LoginException e) {
            logger.error("Error in Get Drop Down Values", e.getMessage());
        }
    }
}