/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.datafactory.versionone;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.versionone.apiclient.ProxyProvider;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.exceptions.V1Exception;
import com.versionone.apiclient.interfaces.IServices;

@Component
public class VersionOneDataFactoryImpl implements VersionOneDataFactory {
	private static Log LOGGER = LogFactory.getLog(VersionOneDataFactoryImpl.class);
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP") // not an IP
	private static final String AGENT_VER = "01.00.00.01";
	private static final String AGENT_NAME = "Hygieia Dashboard - VersionOne Feature Collector";

	protected int pageSize;
	protected int pageIndex;
	protected JSONArray jsonOutputArray;
	protected String basicQuery;
	protected String pagingQuery;
	protected IServices v1Service;

	/**
	 * Default blank constructor
	 */
	public VersionOneDataFactoryImpl() {
	}

	/**
	 * Default constructor, which sets page size to 2000 and page index to 0.
	 */
	public VersionOneDataFactoryImpl(Map<String, String> auth) {
		this.v1Service = new Services(VersionOneAuthentication(auth));
		this.pageSize = 2000;
		this.pageIndex = 0;
	}

	/**
	 * Constructs V1 data factory, but defaults the page size to the page size
	 * parameter given, and the page index to 0.
	 *
	 * @param inPageSize
	 *            A default page size to give the class on construction
	 */
	public VersionOneDataFactoryImpl(int inPageSize, Map<String, String> auth) {
		this.v1Service = new Services(VersionOneAuthentication(auth));
		this.pageSize = inPageSize;
		pageIndex = 0;
	}

	/**
	 * Used for establishing connection to VersionOne based on authentication
	 *
	 * @param auth
	 *            A key-value pairing of authentication values
	 * @return A V1Connector connection instance
	 */
	private V1Connector VersionOneAuthentication(Map<String, String> auth) {
		V1Connector connector = null;

		try {
			if (!auth.get("v1ProxyUrl").equalsIgnoreCase(null)) {
				ProxyProvider proxyProvider = new ProxyProvider(new URI(
						auth.get("v1ProxyUrl")), "", "");

				connector = V1Connector
						.withInstanceUrl(auth.get("v1BaseUri"))
						.withUserAgentHeader(AGENT_NAME, AGENT_VER)
						.withAccessToken(auth.get("v1AccessToken"))
						.withProxy(proxyProvider).build();
			} else {
				connector = V1Connector
						.withInstanceUrl(auth.get("v1BaseUri"))
						.withUserAgentHeader(AGENT_NAME, AGENT_VER)
						.withAccessToken(auth.get("v1AccessToken")).build();
			}
		} catch (MalformedURLException | V1Exception e) {
			LOGGER.error("There was a problem connecting and authenticating with VersionOne:\n"
					+ e.getMessage()
					+ " | "
					+ e.getCause()
					+ " | "
					+ Arrays.toString(e.getStackTrace()));
		} catch (URISyntaxException e) {
			LOGGER.error("There was a problem connecting and authenticating with VersionOne while creating a proxy:\n"
					+ e.getMessage()
					+ " | "
					+ e.getCause()
					+ " | "
					+ Arrays.toString(e.getStackTrace()));
		} catch (Exception e) {
			LOGGER.error("There was an unexpected problem connecting and authenticating with VersionOne:\n"
					+ e.getMessage()
					+ " | "
					+ e.getCause()
					+ " | "
					+ Arrays.toString(e.getStackTrace()));
		}

		return connector;
	}

	/**
	 * Sets the local query value on demand based on a given basic query.
	 *
	 * @param query
	 *            A query in YAML syntax as a String
	 * @return The saved YAML-syntax basic query
	 */
	public String buildBasicQuery(String query) {
		this.setBasicQuery(query);
		return this.getBasicQuery();
	}

	/**
	 * Creates a query on demand based on a given basic query and a specified
	 * page index value. It is recommended to use this method in a loop to
	 * ensure all pages are covered.
	 *
	 * @param inPageIndex
	 *            A given query's current page index, from 0-oo
	 * @return A JSON-formatted response
	 */
	public String buildPagingQuery(int inPageIndex) {
		this.setPageIndex(inPageIndex);
		String pageFilter = "\npage:\n" + "   size: " + pageSize + "\n"
				+ "   start: " + pageIndex;
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
	 */
	public JSONArray getPagingQueryResponse() {
		synchronized (this.v1Service) {
			this.setJsonOutputArray(this.v1Service.executePassThroughQuery(this
					.getPagingQuery()));
		}

		return this.getJsonOutputArray();
	}

	/**
	 * Runs the VersionOneConnection library tools against a given
	 * YAML-formatted query. This requires a pre-formatted basic query
	 * (single-use).
	 *
	 * @return A formatted JSONArray response
	 */
	public JSONArray getQueryResponse() {
		synchronized (this.v1Service) {
			this.setJsonOutputArray(this.v1Service.executePassThroughQuery(this
					.getBasicQuery()));
		}

		return this.getJsonOutputArray();
	}

	/**
	 * Mutator method for page index.
	 *
	 * @param pageIndex
	 *            Page index of query
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * Mutator method for page size.
	 *
	 * @param pageIndex
	 *            Page index of query
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
	 * Accessor method for JSON response output array.
	 *
	 * @return JSON response array from VersionOne
	 */
	private JSONArray getJsonOutputArray() {
		return jsonOutputArray;
	}

	/**
	 * Mutator method for JSON response output array.
	 *
	 * @return JSON response array from VersionOne
	 */
	private void setJsonOutputArray(String stringResult) {
		JSONParser parser = new JSONParser();
		Object nativeRs = null;
		try {
			nativeRs = parser.parse(stringResult);
		} catch (ParseException e) {
			LOGGER.error("There was a problem parsing the JSONArray response value from the source system:\n"
					+ e.getMessage()
					+ " | "
					+ e.getCause()
					+ " | "
					+ Arrays.toString(e.getStackTrace()));
		}
		JSONArray canonicalRs = (JSONArray) nativeRs;
		this.jsonOutputArray = canonicalRs;
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
	 * @param Basic
	 *            VersionOne YAML query
	 */
	private void setBasicQuery(String basicQuery) {
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
	 * @param pagingQuery
	 *            The paged YAML query
	 */
	private void setPagingQuery(String pagingQuery) {
		this.pagingQuery = pagingQuery;
	}

	/**
	 * Used for testing: Accessor Method to get currently set page size
	 */
	public int getPageSize() {
		return this.pageSize;
	}
}
