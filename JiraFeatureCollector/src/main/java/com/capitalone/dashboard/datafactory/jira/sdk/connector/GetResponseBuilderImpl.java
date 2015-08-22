package com.capitalone.dashboard.datafactory.jira.sdk.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.capitalone.dashboard.datafactory.jira.sdk.util.SystemInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

/**
 * {@inheritDoc}
 * 
 * @author kfk884
 * 
 */
public class GetResponseBuilderImpl extends BaseConnectionImpl implements
		GetResponseBuilder {
	private static final Log LOGGER = LogFactory
			.getLog(GetResponseBuilderImpl.class);
	private static final int TIMEOUT = 120000;

	/**
	 * Constructor which inherits artifacts from super class
	 */
	public GetResponseBuilderImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.GetResponseBuilder#getResponse(com
	 * .google.api.client.http.HttpRequestFactory, java.lang.String)
	 */
	@Override
	public JSONObject getResponse(HttpRequestFactory rqFactory, String query) {
		// Declare local HTTP, Credential, and response object related artifacts
		JSONObject canonicalRs = new JSONObject();
		HttpRequest request = null;
		SystemInfo userAgent = new SystemInfo();
		HttpResponse nativeRs = null;
		GenericUrl url = new GenericUrl();

		try {
			url = new GenericUrl(super.baseUrl + super.apiContextPath + query);
			request = rqFactory.buildGetRequest(url);
			request.setHeaders(new HttpHeaders().setAuthorization(
					"Basic " + super.credentials).setUserAgent(
					userAgent.generateApplicationUseHeader()));
			request.setConnectTimeout(TIMEOUT);
			request.setReadTimeout(TIMEOUT);
			LOGGER.info("getResponse: url = " + url);
			synchronized (request) {
				nativeRs = request.execute();
			}
		} catch (IOException | NullPointerException e) {
			LOGGER.error("There was a problem connecting to Jira with a given query:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} catch (IllegalArgumentException e) {
			LOGGER.error("The given query was malformed\nPlease re-attempt the query without spaces or illegal HTTP characters handled by REST:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} catch (Exception e) {
			LOGGER.error("An unexpected exception was caught while generating the HttpRequest artifact to talk with Jira:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} finally {
			try {
				canonicalRs = this.toCanonicalRs(nativeRs);
				nativeRs.disconnect();
				LOGGER.info("Jira web response message has been successfully generated and transformed");
			} catch (IOException e) {
				LOGGER.error("There was a problem retrieving Jira data from the input stream: "
						+ e.getMessage()
						+ "\n"
						+ Arrays.toString(e.getStackTrace()));
			}
		}
		return canonicalRs;
	}

	@Override
	public JSONArray getResponseArray(HttpRequestFactory rqFactory, String query) {
		// Declare local HTTP, Credential, and response object related artifacts
		JSONArray canonicalRs = new JSONArray();
		HttpRequest request = null;
		SystemInfo userAgent = new SystemInfo();
		HttpResponse nativeRs = null;
		GenericUrl url = new GenericUrl();

		try {
			url = new GenericUrl(super.baseUrl + super.apiContextPath + query);
			request = rqFactory.buildGetRequest(url);
			request.setHeaders(new HttpHeaders().setAuthorization(
					"Basic " + super.credentials).setUserAgent(
					userAgent.generateApplicationUseHeader()));
			request.setConnectTimeout(TIMEOUT);
			request.setReadTimeout(TIMEOUT);
			synchronized (request) {
				nativeRs = request.execute();
			}
		} catch (IOException | NullPointerException e) {
			LOGGER.error("There was a problem connecting to Jira with a given query:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} catch (IllegalArgumentException e) {
			LOGGER.error("The given query was malformed\nPlease re-attempt the query without spaces or illegal HTTP characters handled by REST:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} catch (Exception e) {
			LOGGER.error("An unexpected exception was caught while generating the HttpRequest artifact to talk with Jira:\n"
					+ e.getMessage()
					+ "\n"
					+ Arrays.toString(e.getStackTrace()));
			return canonicalRs;
		} finally {
			try {
				canonicalRs = this.toCanonicalRsArray(nativeRs);
				nativeRs.disconnect();
				LOGGER.info("Jira web response message has been successfully generated and transformed");
			} catch (IOException e) {
				LOGGER.error("There was a problem retrieving Jira data from the input stream: "
						+ e.getMessage()
						+ "\n"
						+ Arrays.toString(e.getStackTrace()));
			}
		}
		return canonicalRs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.capitalone.jira.client.connector.GetResponseBuilder#getResponseOAuth
	 * (com .google.api.client.http.HttpRequestFactory, java.lang.String)
	 */
	public JSONObject getResponseOAuth(HttpRequestFactory rqFactory,
			String query) throws NotImplementedException {
		// TODO This is currently not implemented for OAuth functionality
		throw new NotImplementedException("currently not implemented for OAuth functionality");
	}

	/**
	 * Converts an HttpResponse message content stream into a valid JSONObject
	 * for file consumption
	 * 
	 * @param content
	 *            HttpResponse message content as an input stream
	 * @return A valid JSONObject from the HttpResponse message content
	 */
	private JSONObject toCanonicalRs(HttpResponse nativeRs) throws IOException {
		JSONObject canonicalRs = new JSONObject();
		StringBuilder builder = new StringBuilder();
		InputStream content = nativeRs.getContent();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(content));

		for (String line = null; (line = bufferedReader.readLine()) != null;) {
			builder.append(line).append("\n");
		}

		Object obj = JSONValue.parse(builder.toString());
		canonicalRs = (JSONObject) obj;

		return canonicalRs;
	}

	/**
	 * Converts an HttpResponse message content stream into a valid JSONArray
	 * for file consumption
	 * 
	 * @param content
	 *            HttpResponse message content as an input stream
	 * @return A valid JSONArray from the HttpResponse message content
	 */
	private JSONArray toCanonicalRsArray(HttpResponse nativeRs)
			throws IOException {
		JSONArray canonicalRs = new JSONArray();
		StringBuilder builder = new StringBuilder();
		InputStream content = nativeRs.getContent();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(content));

		for (String line = null; (line = bufferedReader.readLine()) != null;) {
			builder.append(line).append("\n");
		}

		Object obj = JSONValue.parse(builder.toString());
		canonicalRs = (JSONArray) obj;

		return canonicalRs;
	}
}
