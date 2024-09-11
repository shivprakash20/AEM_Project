package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Enumeration;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Form Rest EndPoint Servlet",
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/restEndPoint"})
public class AEMFormRestEndPoint extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(AEMFormRestEndPoint.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            Enumeration<String> params = request.getParameterNames();

            JSONObject jsonObject = new JSONObject();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                String data = request.getParameter(paramName);
                jsonObject.put(paramName, data);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.valueOf(jsonObject));

        } catch (IOException | JSONException e) {
            LOGGER.error("Error in response writing :{}", e.getMessage());
        }
    }

}
