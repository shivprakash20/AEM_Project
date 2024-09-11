package com.adobe.learning.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=AEM Scholar Servlet for Data Source and FDM",
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/aemScholarServlet"})
public class AEMScholarServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(AEMScholarServlet.class);

    @Reference
    DataSource dataSource;

    /***
     * Get Method to fetch scholar details with ID
     * @param request  Fetch ID from request parameters.
     * @param response Write JSON Response of Data.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            JSONArray jsonArrayData = getSingleScholarData(id);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.valueOf(jsonArrayData));

        } catch (IOException e) {
            LOGGER.error("Error in response writing :{}", e.getMessage());
        }
    }

    /***
     * Post Method to update scholar data in MYSQL Database.
     * @param request  Fetch all Form parameter to insert.
     * @param response Response as true after successful insertion.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        LOGGER.info("### Inside doPost");
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AEM_SQL.SCHOLAR (ID, NAME, DOB, GENDER, GRADE) VALUES (?, ?, ?, ?, ?)")) {

            Map<String, String[]> allParameter = request.getParameterMap();
            Map.Entry<String, String[]> paramEntry = allParameter.entrySet().iterator().next();
            String allValues = paramEntry.getKey();
            JSONObject jsonObject = new JSONObject(allValues);

            preparedStatement.setInt(1, jsonObject.getInt("id"));
            preparedStatement.setString(2, jsonObject.getString("name"));
            preparedStatement.setString(3, jsonObject.getString("dob"));
            preparedStatement.setString(4, jsonObject.getString("gender"));
            preparedStatement.setString(5, jsonObject.getString("grade"));
            preparedStatement.executeUpdate();
            connection.commit();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.valueOf(true));
        } catch (IOException | SQLException | JSONException e) {
            LOGGER.error("Error in response writing :{}", e.getMessage());
        }
    }

    /***
     * Return JSON Array Data for specific ID
     * @return Return all JSON Array Data
     * @param id Take input as ID
     */
    public JSONArray getSingleScholarData(int id) {
        LOGGER.debug("### Inside my getData of aem_sql.scholar");
        JSONArray jsonArray = new JSONArray();
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AEM_SQL.SCHOLAR WHERE ID = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            jsonArray = getJsonData(resultSet);
        } catch (SQLException | JSONException e) {
            LOGGER.error("SQL Exception Occurred :{}", e.getMessage());
        }
        return jsonArray;
    }

    /***
     * Getting the connection to Data Source MYSQL.
     * @return the connection object.
     */
    public Connection getConnection() {
        LOGGER.debug("### Getting Connection :");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            LOGGER.debug("Got connection");
        } catch (Exception e) {
            LOGGER.debug("Not able to get connection {}", e.getMessage());
        }
        return connection;
    }

    /***
     *  Fetch data in form of JSON Array from resultSet.
     * @param resultSet Take result of executed query.
     * @return Data in form of JSON Array.
     * @throws SQLException Throw SQL Exception.
     * @throws JSONException Throw JSON Exception.
     */
    public JSONArray getJsonData(ResultSet resultSet) throws SQLException, JSONException {
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        while (resultSet.next()) {
            int numColumns = resultSetMetaData.getColumnCount();
            JSONObject jsonObject = new JSONObject();
            eachRowData(numColumns, resultSet, resultSetMetaData, jsonObject);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /***
     * Fetch SQL data for all Column and Put in JSON Object.
     * @param numColumns Column in SQL Table.
     * @param resultSet Result of Executed Query.
     * @param resultSetMetaData ResultSet Metadata
     * @param jsonObject New Json Object
     * @throws SQLException Throw SQL Exception
     * @throws JSONException Throw JSON Exception
     */
    public void eachRowData(int numColumns, ResultSet resultSet, ResultSetMetaData resultSetMetaData, JSONObject jsonObject) throws SQLException, JSONException {
        for (int i = 1; i < numColumns + 1; i++) {
            String column_name = resultSetMetaData.getColumnName(i);
            if (resultSetMetaData.getColumnType(i) == Types.ARRAY) {
                jsonObject.put(column_name, resultSet.getArray(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.BOOLEAN) {
                jsonObject.put(column_name, resultSet.getBoolean(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.DOUBLE) {
                jsonObject.put(column_name, resultSet.getDouble(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.FLOAT) {
                jsonObject.put(column_name, resultSet.getFloat(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.INTEGER) {
                jsonObject.put(column_name, resultSet.getInt(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.NVARCHAR) {
                jsonObject.put(column_name, resultSet.getNString(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.VARCHAR) {
                jsonObject.put(column_name, resultSet.getString(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.SMALLINT) {
                jsonObject.put(column_name, resultSet.getInt(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.DATE) {
                jsonObject.put(column_name, resultSet.getDate(column_name));
            } else if (resultSetMetaData.getColumnType(i) == Types.TIMESTAMP) {
                jsonObject.put(column_name, resultSet.getTimestamp(column_name));
            } else {
                jsonObject.put(column_name, resultSet.getObject(column_name));
            }
        }
    }
}
