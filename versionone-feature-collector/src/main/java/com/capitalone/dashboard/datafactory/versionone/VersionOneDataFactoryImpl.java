/*************************
 * DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************DA-BOARD-LICENSE-END
 *********************************/

package com.capitalone.dashboard.datafactory.versionone;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.versionone.apiclient.ProxyProvider;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.exceptions.V1Exception;
import com.versionone.apiclient.interfaces.IServices;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class VersionOneDataFactoryImpl implements VersionOneDataFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionOneDataFactoryImpl.class);

    protected int pageSize;
    protected int pageIndex;
    protected JSONArray jsonOutputArray;
    protected String basicQuery;
    protected String pagingQuery;
    protected IServices v1Service = null;

    /**
     * Default blank constructor
     */
    public VersionOneDataFactoryImpl() {
        this.pageSize = 2000;
        this.pageIndex = 0;
    }

    /**
     * Default constructor, which sets page size to 2000 and page index to 0.
     *
     * @throws HygieiaException
     */
    public VersionOneDataFactoryImpl(Map<String, String> auth) throws HygieiaException {
        this(2000, auth);
    }

    /**
     * Constructs V1 data factory, but defaults the page size to the page size
     * parameter given, and the page index to 0.
     *
     * @param inPageSize A default page size to give the class on construction
     * @throws HygieiaException
     */
    public VersionOneDataFactoryImpl(int inPageSize, Map<String, String> auth)
            throws HygieiaException {
        this.v1Service = new Services(versionOneAuthentication(auth));
        this.pageSize = inPageSize;
        pageIndex = 0;
    }

    /**
     * Used for establishing connection to VersionOne based on authentication
     *
     * @param auth A key-value pairing of authentication values
     * @return A V1Connector connection instance
     */
    private V1Connector versionOneAuthentication(Map<String, String> auth) throws HygieiaException {
        V1Connector connector;

        try {
            if (!StringUtils.isEmpty(auth.get("v1ProxyUrl"))) {
                ProxyProvider proxyProvider = new ProxyProvider(new URI(auth.get("v1ProxyUrl")), "",
                        "");

                connector = V1Connector.withInstanceUrl(auth.get("v1BaseUri"))
                        .withUserAgentHeader(FeatureCollectorConstants.AGENT_NAME, FeatureCollectorConstants.AGENT_VER)
                        .withAccessToken(auth.get("v1AccessToken"))
                        .withProxy(proxyProvider)
                        .build();
            } else {
                connector = V1Connector.withInstanceUrl(auth.get("v1BaseUri"))
                        .withUserAgentHeader(FeatureCollectorConstants.AGENT_NAME, FeatureCollectorConstants.AGENT_VER)
                        .withAccessToken(auth.get("v1AccessToken")).build();
            }
        } catch (V1Exception ve) {
            throw new HygieiaException("FAILED: VersionOne was not able to authenticate", ve,
                    HygieiaException.INVALID_CONFIGURATION);
        } catch (MalformedURLException | URISyntaxException me) {
            throw new HygieiaException("FAILED: Invalid VersionOne URL.", me,
                    HygieiaException.INVALID_CONFIGURATION);
        }
        return connector;
    }


    /**
     * Creates a query on demand based on a given basic query and a specified
     * page index value. It is recommended to use this method in a loop to
     * ensure all pages are covered.
     *
     * @param inPageIndex A given query's current page index, from 0-oo
     * @return A JSON-formatted response
     */
    public String buildPagingQuery(int inPageIndex) {
        this.setPageIndex(inPageIndex);
        String pageFilter = "\npage:\n" + "   size: " + pageSize + "\n" + "   start: " + pageIndex;
        this.setPagingQuery(this.getBasicQuery() + pageFilter);
        return this.getPagingQuery();
    }

    /**
     * Runs the VersionOneConnection library tools against a given
     * YAML-formatted query. This requires a pre-formatted paged query to run,
     * and will not perform the paging for you - there are other helper methods
     * for this.
     *
     * @return A formatted JSONArray response
     * @throws HygieiaException
     */
    public JSONArray getPagingQueryResponse() throws HygieiaException {
        synchronized (this.v1Service) {
            Object obj = this.v1Service.executePassThroughQuery(this.getPagingQuery());

            if (obj == null) {
                throw new HygieiaException(
                        "FAILED: There was a problem parsing or casting JSON types from a message response",
                        HygieiaException.JSON_FORMAT_ERROR);
            }

            if (obj.toString().equalsIgnoreCase("{\"error\":\"Unauthorized\"}")) {
                throw new HygieiaException(
                        "FAILED: There was a problem authenticating with VersionOne",
                        HygieiaException.INVALID_CONFIGURATION);
            }

            return makeJsonOutputArray(obj.toString());
        }
    }

    /**
     * Runs the VersionOneConnection library tools against a given
     * YAML-formatted query. This requires a pre-formatted basic query
     * (single-use).
     *
     * @return A formatted JSONArray response
     */
    public JSONArray getQueryResponse() throws HygieiaException {
        synchronized (this.v1Service) {
            Object obj = this.v1Service.executePassThroughQuery(this.getBasicQuery());

            if (obj == null) {
                throw new HygieiaException(
                        "FAILED: There was a problem parsing or casting JSON types from a message response",
                        HygieiaException.JSON_FORMAT_ERROR);
            }

            if (obj.toString().equalsIgnoreCase("{\"error\":\"Unauthorized\"}")) {
                throw new HygieiaException(
                        "FAILED: There was a problem authenticating with VersionOne",
                        HygieiaException.INVALID_CONFIGURATION);
            }

            return makeJsonOutputArray(obj.toString());
        }
    }

    /**
     * Mutator method for page index.
     *
     * @param pageIndex Page index of query
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * Mutator method for page size.
     *
     * @param pageSize Page index of query
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Accessor method for page index.
     *
     * @return Page index of query
     */
    public int getPageIndex() {
        return this.pageIndex;
    }

    /**
     * Mutator method for JSON response output array.
     */
    private JSONArray makeJsonOutputArray(String stringResult) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONArray) parser.parse(stringResult);
        } catch (ParseException | ClassCastException e) {
            LOGGER.error(
                    "There was a problem parsing the JSONArray response value from the source system:\n"
                            + e.getMessage() + " | " + e.getCause());
            return new JSONArray();
        }
    }

    /**
     * Accessor method for basic query formatted object.
     *
     * @return Basic VersionOne YAML query
     */
    public String getBasicQuery() {
        return basicQuery;
    }

    /**
     * Mutator method for basic query formatted object.
     *
     * @param basicQuery VersionOne YAML query
     */
    public void setBasicQuery(String basicQuery) {
        this.basicQuery = basicQuery;
    }

    /**
     * Accessor method for retrieving paged query.
     *
     * @return The paged YAML query
     */
    public String getPagingQuery() {
        return pagingQuery;
    }

    /**
     * Mutator method for setting paged query
     *
     * @param pagingQuery The paged YAML query
     */
    public void setPagingQuery(String pagingQuery) {
        this.pagingQuery = pagingQuery;
    }

    /**
     * Used for testing: Accessor Method to get currently set page size
     */
    public int getPageSize() {
        return this.pageSize;
    }
}
