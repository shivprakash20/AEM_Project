package com.adobe.learning.core.servlets;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

import org.apache.solr.client.solrj.SolrClient;

import java.io.IOException;
import java.util.Random;

/**
 * http://localhost:4502/bin/solrInteraction?solrAction=update
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Save to Solr Description",
        "sling.servlet.paths=" + "/bin/solrInteraction", "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SolrInteraction extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrInteraction.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {

            String solrAction = request.getParameter("solrAction");

            SolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr/learnsolraem").build();
            switch (solrAction) {
                case "update":
                    SolrInputDocument solrInputFields = new SolrInputDocument();
                    Random random = new Random(System.currentTimeMillis());
                    int number = random.nextInt(100000);
                    String formattedNumber = String.format("%05d", number);
                    solrInputFields.addField("id",formattedNumber);
                    solrInputFields.addField("name", "shiv");
                    solrInputFields.addField("age", 28);
                    solrInputFields.addField("location", "vns");
                    solrClient.add(solrInputFields);
                    solrClient.commit(true, true);

                    response.getWriter().write("Document is updated to Solr Successfully");
                    break;
                case "delete":
                    solrClient.deleteByQuery("*");
                    solrClient.commit();
                    response.getWriter().write("Document is deleted from Solr Successfully");
                    break;
                case "search":
                    ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams();
                    modifiableSolrParams.set("q", "name:shiv");
                    modifiableSolrParams.set("start", "0");

                    QueryResponse queryResponse = solrClient.query(modifiableSolrParams);
                    SolrDocumentList solrDocuments = queryResponse.getResults();
                    response.getWriter().write("Number of document searched from Solr is :" + solrDocuments.getNumFound());
                    response.getWriter().write("\n" + solrDocuments.toString());
                    break;
                default:
                    response.getWriter().write("Please give update, delete or search as solrAction Argument.");
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error("Exception occurred: {}", e.getMessage());
        }
    }
}
