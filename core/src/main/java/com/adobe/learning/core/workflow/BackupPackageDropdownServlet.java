package com.adobe.learning.core.workflow;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Vishnu
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Package Drop Down",
        "sling.servlet.paths=" + "/bin/backupPackageDropdown", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class BackupPackageDropdownServlet extends SlingSafeMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(BackupPackageDropdownServlet.class);

    ResourceResolver resourceResolver;
    Resource pathResource;
    ValueMap valueMap;
    List<Resource> resourceList;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            resourceResolver = request.getResourceResolver();
            pathResource = request.getResource();
            resourceList = new ArrayList<>();

            /* All Tag Available Inside the Service Line */
            String packagePath = Objects.requireNonNull(pathResource.getChild("datasource")).getValueMap().get("contentPackagePath", String.class);
            assert packagePath != null;
            Resource packageResource = request.getResourceResolver().getResource(packagePath);

            assert packageResource != null;
            for (Resource packageChild : packageResource.getChildren()) {
                valueMap = new ValueMapDecorator(new HashMap<>());
                if(packageChild.getName().endsWith("_Backup.zip")) {
                    valueMap.put("value", packageChild.getPath());
                    valueMap.put("text", packageChild.getName());
                    resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", valueMap));
                }
            }

            /*Create a DataSource that is used to populate the drop-down control*/
            DataSource dataSource = new SimpleDataSource(resourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (Exception e) {
            logger.error("Error in Get Drop Down Values", e);
        }
    }
}
