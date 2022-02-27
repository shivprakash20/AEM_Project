package com.adobe.learning.core.service.impl;

import com.adobe.learning.core.service.UnLockServletFormData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

@Component(service = UnLockServletFormData.class, immediate = true)
public class UnlockServletFormDataImpl implements UnLockServletFormData {

    private final Logger logger = LoggerFactory.getLogger(UnlockServletFormDataImpl.class);

    @Reference
    DataSource dataSource;

    @Override
    public JSONObject getUserData(int givenId) {
        JSONObject returnJsonData = new JSONObject();
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM servlet_datasource.servlet_forms where Id = " + givenId;
            logger.debug("Getting Result Set :{}", query);
            ResultSet resultSet = statement.executeQuery(query);
            returnJsonData = getJsonObjectData(resultSet);
        } catch (SQLException | JSONException e) {
            logger.error("SQL Exception Occurred :{}", e.getMessage());
        }
        return returnJsonData;
    }

    @Override
    public JSONArray getALLData() {
        logger.debug("### Inside my getData of servlet_datasource.servlet.forms");
        JSONArray jsonArray = new JSONArray();
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM servlet_datasource.servlet_forms";
            logger.debug("Getting Result Set :{}", query);
            ResultSet resultSet = statement.executeQuery(query);
            jsonArray = getJsonData(resultSet);
        } catch (SQLException | JSONException e) {
            logger.error("SQL Exception Occurred :{}", e.getMessage());
        }
        return jsonArray;
    }

    /***
     * Getting the connection to Data Source MYSQL.
     * @return the connection object.
     */
    public Connection getConnection() {
        logger.debug("### Getting Connection :");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            logger.debug("Got connection");
        } catch (Exception e) {
            logger.debug("Not able to get connection {}", e.getMessage());
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
     * Result of User Details for given UserId.
     * @param resultSet Result of executed query.
     * @return Data of User on the basis of UserId.
     * @throws SQLException Throw SQL Exception.
     * @throws JSONException Throw JSON Exception.
     */
    public JSONObject getJsonObjectData(ResultSet resultSet) throws SQLException, JSONException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        JSONObject jsonData = new JSONObject();
        int numColumns = resultSetMetaData.getColumnCount();
        while (resultSet.next()) {
            eachRowData(numColumns, resultSet, resultSetMetaData, jsonData);
        }
        return jsonData;
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
