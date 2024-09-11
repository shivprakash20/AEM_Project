package com.adobe.learning.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Shiv
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Json Data in dynamic Dropdown",
        "sling.servlet.paths=" + "/bin/jsonDataDropdown", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class JsonDataDropdownServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataDropdownServlet.class);

    transient ResourceResolver resourceResolver;
    transient Resource pathResource;
    transient ValueMap valueMap;
    transient List<Resource> resourceList;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        resourceResolver = request.getResourceResolver();
        pathResource = request.getResource();
        resourceList = new ArrayList<>();

        try {
            /* Getting AEM Tags Path given on datasource Node */
            String jsonDataPath = Objects.requireNonNull(pathResource.getChild("datasource")).getValueMap().get("jsonDataPath", String.class);
            assert jsonDataPath != null;
            //Getting Tag Resource using JsonDataPath
            Resource jsonResource = request.getResourceResolver().getResource(jsonDataPath);
            assert jsonResource != null;
            //Getting Asset from jsonResource
            Asset jsonAsset = jsonResource.adaptTo(Asset.class);
            assert jsonAsset != null;
            //Converting Asset into input stream for JSON Object
            InputStream inputStream = jsonAsset.getOriginal().getStream();

            StringBuilder stringBuilder = new StringBuilder();
            String eachLine;
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            while ((eachLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(eachLine);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            Iterator<String> jsonKeys = jsonObject.keys();
            //Iterating JSON Objects over key
            while (jsonKeys.hasNext()) {
                String jsonKey = jsonKeys.next();
                String jsonValue = jsonObject.getString(jsonKey);

                valueMap = new ValueMapDecorator(new HashMap<>());
                valueMap.put("value", jsonKey);
                valueMap.put("text", jsonValue);
                resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", valueMap));
            }

            /*Create a DataSource that is used to populate the drop-down control*/
            DataSource dataSource = new SimpleDataSource(resourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (JSONException | IOException e) {
            LOGGER.error("Error in Json Data Exporting : {}", e.getMessage());
        }
    }
}
