package com.adobe.learning.core.workflow;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;

@Component(service = WorkflowProcess.class, property = {Constants.SERVICE_DESCRIPTION + "= Process Step for Email Notification",
        Constants.SERVICE_VENDOR + "= workflow.com",
        "process.label" + "= Migrated Package Email Process"})
public class MigratedPackageEmailProcess implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(MigratedPackageEmailProcess.class);
    private static final String EMAIL_PROFILE = "./profile/email";
    List<String> emailRecipientsList;

    @Reference
    EmailService emailService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);

        String processArgs = metaDataMap.get("PROCESS_ARGS", String.class);
        String[] processArgsParam = processArgs.split(":");
        String recipients = processArgsParam[0];
        String templatePath = processArgsParam[1];
        assert resourceResolver != null;
        getEmailRecipients(resourceResolver, recipients);

        if(!emailRecipientsList.isEmpty()) {
            final String[] emailList = emailRecipientsList.toArray(new String[0]);
            final Map<String, String> emailParams = new HashMap<>();
            emailParams.put("client","workflow");
            emailService.sendEmail(templatePath, emailParams, emailList);
        }
    }

    private void getEmailRecipients(ResourceResolver resourceResolver, String userGroupDetails) {
        try {
            emailRecipientsList = new ArrayList<>();
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            // If Data is a Group
            if (userManager != null && Objects.requireNonNull(userManager.getAuthorizable(userGroupDetails)).isGroup()) {
                Group groupData = (Group) userManager.getAuthorizable(userGroupDetails);
                if (groupData != null) {
                    Iterator<Authorizable> members = groupData.getMembers();
                    while (members.hasNext()) {
                        Object nextMember = members.next();
                        if (nextMember instanceof User) {
                            User user = (User) nextMember;
                            getUserEmail(userManager, user);
                        }
                    }
                }
            } else {
                // If Data is User
                assert userManager != null;
                User user = (User) userManager.getAuthorizable(userGroupDetails);
                if (user != null) {
                    getUserEmail(userManager, user);
                }
            }
        } catch (RepositoryException e) {
            logger.error("Error in Email Notification getEmailRecipients Method RepositoryException {}", e.getMessage());
        }
    }

    private void getUserEmail(UserManager userManager, User user) {
        try {
            Authorizable userAuthorization = userManager.getAuthorizable(user.getID());
            if (userAuthorization != null && userAuthorization.hasProperty(EMAIL_PROFILE)) {
                Value[] properties = userAuthorization.getProperty(EMAIL_PROFILE);
                if (properties != null) {
                    for (Value property : properties) {
                        emailRecipientsList.add(property.toString());
                    }
                }
            }
        } catch (RepositoryException e) {
            logger.error("Error in Email Notification getUserEmail Method RepositoryException {}", e.getMessage());
        }
    }
}
