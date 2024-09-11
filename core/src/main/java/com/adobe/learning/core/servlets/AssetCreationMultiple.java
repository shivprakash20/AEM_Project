package com.adobe.learning.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Asset Creation",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/assetCreation"
})
public class AssetCreationMultiple extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
        try {
            Resource csvResource = request.getResourceResolver().getResource("/content/dam/learning/Sample-Spreadsheet-10-rows.csv");
            assert csvResource != null;
            Asset csvAsset = csvResource.adaptTo(Asset.class);
            assert csvAsset != null;
            InputStream inputStream = csvAsset.getOriginal().getStream();

            ResourceResolver resourceResolver = request.getResourceResolver();
            Asset asset = createAsset(resourceResolver, inputStream, "/content/dam/learning");

            ResourceResolver resolver = request.getResourceResolver();
            Asset report = createAsset(resolver, inputStream, "/content/dam/learning");

            response.getWriter().write("Assets are :" + asset.toString() + " Report are" + report.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Asset createAsset(ResourceResolver resourceResolver, InputStream inputStream, String path) {
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
        assert assetManager != null;
        return assetManager.createAsset(path, inputStream, "text/csv", true);
    }
}
