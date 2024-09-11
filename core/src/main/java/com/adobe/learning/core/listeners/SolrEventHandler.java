package com.adobe.learning.core.listeners;

import com.day.cq.replication.ReplicationAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shiv
 */
@Component(service = EventHandler.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= DAM Asset Events for Solr Indexing",
        EventConstants.EVENT_TOPIC + "=" + ReplicationAction.EVENT_TOPIC,
        EventConstants.EVENT_FILTER + "=(paths=/content/dam/*)"})

public class SolrEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrEventHandler.class);
    private static final String SYSTEM_USER = "system_user";
    private static final String METADATA_CONSTANT = "/jcr:content/metadata";

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    public void handleEvent(Event event) {
        try {
            //Getting Replication Options from Event
            ReplicationAction replicationAction = ReplicationAction.fromEvent(event);
            String assetPath = StringUtils.EMPTY;

            //Getting Resource Resolver from system user for ValueMap
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, SYSTEM_USER);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            if (replicationAction != null) {
                //Getting asset path which we are publishing
                assetPath = replicationAction.getPath();

                //Getting Resource and ValueMap for uuid
                Resource parentResource = resourceResolver.getResource(assetPath);
                assert parentResource != null;
                ValueMap parentValueMap = parentResource.getValueMap();

                //Getting Resource and ValueMap for asset Metadata
                Resource resource = resourceResolver.getResource(assetPath + METADATA_CONSTANT);
                assert resource != null;
                ValueMap valueMap = resource.getValueMap();

                //Connecting to Solr Collection using Solr Client
                SolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr/learnsolraem").build();

                //Add all properties that we want to send to solr
                SolrInputDocument solrInputFields = new SolrInputDocument();
                solrInputFields.addField("id", parentValueMap.get("jcr:uuid", String.class));
                solrInputFields.addField("path", assetPath);
                solrInputFields.addField("title", valueMap.get("dc:title", String.class));
                solrInputFields.addField("description", valueMap.get("dc:description", String.class));
                solrClient.add(solrInputFields);

                //Commit the data to solr
                solrClient.commit(true, true);
                LOGGER.info("Document is updated to Solr Successfully");
            }

            LOGGER.info("Event Path : {}", assetPath);
        } catch (SolrServerException | IOException | LoginException e) {
            LOGGER.error("Error in Replication Event : {}", e.getMessage());
        }
    }
}
