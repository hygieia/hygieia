package com.capitalone.dashboard.datafactory.versionone;

import org.json.simple.JSONArray;

import com.capitalone.dashboard.datafactory.DataFactory;

/**
 * Interface for VersionOne data connection. An implemented class should be able to create a formatted request,
 * and retrieve a response in JSON syntax from that request to VersionOne.
 *
 * @author KFK884
 *
 */
public interface VersionOneDataFactory extends DataFactory{
	public String buildBasicQuery(String query);

	public String buildPagingQuery(int inPageIndex);

	public JSONArray getPagingQueryResponse();

	public JSONArray getQueryResponse();

	public String getBasicQuery();

	public String getPagingQuery();

	public int getPageIndex();

}
