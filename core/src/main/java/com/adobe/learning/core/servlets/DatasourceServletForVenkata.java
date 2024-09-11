package com.adobe.learning.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Datasource Servlet to get all the pages list as dropdown of Dialog",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/datasourceservlettogetpagesForVenkata"})
public class DatasourceServletForVenkata extends SlingSafeMethodsServlet {

    private static final long serialVersionUid = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException, IOException {

        List<Resource> valueMapResourceList = new ArrayList<>();

        try {
            getValueMapResourceList(valueMapResourceList, req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataSource dataSource = new SimpleDataSource(valueMapResourceList.iterator());
        req.setAttribute(DataSource.class.getName(), dataSource);


    }

    private void getValueMapResourceList(List<Resource> valueMapResourceList, SlingHttpServletRequest req) throws IOException, JSONException {


        ResourceResolver resourceResolver = req.getResourceResolver();
        /*==================================================================*/
        Resource jcrdatanode = resourceResolver.getResource("/content/dam/learning/json_array.json/jcr:content/renditions/original/jcr:content");
        ValueMap jcrdatanodeValueMap = jcrdatanode.adaptTo(ValueMap.class);
        InputStream content = jcrdatanodeValueMap.get("jcr:data", InputStream.class);

        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(
                content, StandardCharsets.UTF_8));

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        JSONObject wholeJson = new JSONObject(sb.toString());

        //extracting data array from json string
        JSONArray ja_data = wholeJson.getJSONArray("data");
        int length = ja_data.length();


        /*==================================================================*/

        Resource parentResource = resourceResolver.getResource("/content/we-retail/us/en");
        Iterator<Resource> pageResourceIterator = parentResource.getChildren().iterator();

        //loop to get all json objects from data json array
        for(int i=0; i<length; i++) {
            JSONObject jsonObj = ja_data.getJSONObject(i);

            Iterator jsoObjKeyIterator = jsonObj.keys();


            while (jsoObjKeyIterator.hasNext()) {

                String actualKey = jsoObjKeyIterator.next().toString();
                String actualValue = jsonObj.get(actualKey).toString();
                Map<String, Object> map = new HashMap<>();
                map.put("text", actualValue);
                map.put("title", actualKey);
                ValueMap valueMap = new ValueMapDecorator(map);

                Resource valuemapResource = new ValueMapResource(req.getResourceResolver(), new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap);
                valueMapResourceList.add(valuemapResource);
            }
        }
    }
}

