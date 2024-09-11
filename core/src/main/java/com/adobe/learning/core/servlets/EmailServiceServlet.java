package com.adobe.learning.core.servlets;

import com.adobe.acs.commons.email.EmailService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Mail Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/EmailServiceServlet",})
public class EmailServiceServlet extends SlingSafeMethodsServlet {

    @Reference
    private EmailService emailService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            String mailTemp = "/etc/notification/email/acs-commons/email-testing.txt";

            String[] participents = new String[]{"hpshivprakash@gmail.com"};

            final Map<String, String> emailParams = new HashMap<>();
            emailParams.put("client", "sendinblue.com");

            List<String> participantList = emailService.sendEmail(mailTemp, emailParams, participents);

            response.getWriter().append("Successfully Send Email !!!!! \n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

