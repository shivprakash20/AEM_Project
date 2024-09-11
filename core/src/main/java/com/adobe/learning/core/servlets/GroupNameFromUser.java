package com.adobe.learning.core.servlets;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;

/*
This servlet is for full text search on a specific paths
 */

@Component(service = Servlet.class, immediate = true, property = {"sling.servlet.paths=" + "/bin/groupNameFromUser",
        "sling.servlet.methods=" + "GET"
})
public class GroupNameFromUser extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(GroupNameFromUser.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    String finalGroupName = "";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        try {
            //Getting resourceResolver from request
            ResourceResolver resourceResolver = request.getResourceResolver();

            //Getting userId from request parameter
            String userId = request.getParameter("userId");

            //UserManager Object from resourceResolver
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);

            //Getting user from userId
            User user = (User) userManager.getAuthorizable(userId);
            if (user != null) {
                //List of all groups
                Iterator<Group> groups = user.memberOf();
                while (groups.hasNext()) {
                    Group currentGroup = groups.next();
                    String groupID = currentGroup.getID();
                    //Comparing the groups with given groupId
                    if (groupID.equalsIgnoreCase("administrators")) {
                        //Custom Code
                    }
                }
            }

            response.getWriter().write(finalGroupName);

        } catch (Exception e) {
            logger.error("Error in User Details :{}", e.getMessage());
        }
    }
}

