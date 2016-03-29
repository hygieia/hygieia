package com.capitalone.dashboard.datafactory.versionone;

import org.json.simple.JSONArray;

import com.capitalone.dashboard.misc.HygieiaException;
/**
 * Interface for VersionOne data connection. An implemented class should be able to create a formatted request,
 * and retrieve a response in JSON syntax from that request to VersionOne.
 *
 * @author KFK884
 *
 */
public interface VersionOneDataFactory {
	String buildPagingQuery(int inPageIndex);

	JSONArray getPagingQueryResponse() throws HygieiaException;

	JSONArray getQueryResponse() throws HygieiaException;

	String getBasicQuery();

	String getPagingQuery();

	int getPageIndex();

}
