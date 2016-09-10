package com.capitalone.dashboard.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

/**
 * Provides dynamic variable access to the available source system queries for
 * the feature widget.
 * 
 * @author kfk884
 * 
 */
@Component
public class FeatureWidgetQueries {
	private final STGroup folder;

	/**
	 * Constructs the source system query configuration class, based on system
	 * settings.
	 * 
	 * @param featureSettings
	 *            Feature collector system settings
	 */
	@Autowired
	public FeatureWidgetQueries(FeatureSettings featureSettings) {
		this.folder = new STGroupDir(featureSettings.getQueryFolder(), '$', '$');
	}

	/**
	 * Retrieves source system queries based on the query name (without the file
	 * type) and a specified change date parameter.
	 * 
	 * @param changeDatePara
	 *            The change date specified from which to pull data with a given
	 *            query template.
	 * @param queryName
	 *            The source system query name (without the file type).
	 * @return A given source system query, in String format.
	 */
	public String getQuery(String changeDatePara, String queryName) {
		ST st = folder.getInstanceOf(queryName);
		st.add("changeDate", changeDatePara);
		String query = st.render();

		return query;
	}

	/**
	 * Retrieves source system queries based on the query name (without the file
	 * type). This will only grab a non-parametered query.
	 * 
	 * @param queryName
	 *            The source system query name (without the file type).
	 * @return A given source system query, in String format.
	 */
	public String getQuery(String queryName) {
		ST st = folder.getInstanceOf(queryName);
		String query = st.render();

		return query;
	}

	/**
	 * Retrieves source system queries based on the query name (without the file
	 * type) and a specified epic key parameter.
	 * 
	 * @param epicKeyParam
	 *            The Epic key for a given issue.
	 * @param queryName
	 *            The source system query name (without the file type).
	 * @return A given source system query, in String format.
	 */
	public String getEpicQuery(String epicKeyParam, String queryName) {
		ST st = folder.getInstanceOf(queryName);
		st.add("epicKey", epicKeyParam);
		String query = st.render();

		return query;
	}
	
	public String getEpicQuery(List<String> epicKeysParam, String queryName) {
		ST st = folder.getInstanceOf(queryName);
		st.add("epicKeys", epicKeysParam);
		String query = st.render();

		return query;
	}

	/**
	 * Retrieves source system queries based on the query name (without the file
	 * type) and a specified change date parameter.
	 * 
	 * @param changeDatePara
	 *            The change date specified from which to pull data with a given
	 *            query template.
	 * @param issueType
	 *            The Jira IssueType specified from which to pull data with a
	 *            given query template.
	 * @param queryName
	 *            The source system query name (without the file type).
	 * @return A given source system query, in String format.
	 */
	public String getStoryQuery(String changeDatePara, String issueType, String queryName) {
		ST st = folder.getInstanceOf(queryName);
		st.add("changeDate", changeDatePara);
		st.add("issueType", issueType);
		String query = st.render();

		return query;
	}
}
