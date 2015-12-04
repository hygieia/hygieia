package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SuppressWarnings("PMD.SingularField")
public class FeatureWidgetQueries {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureWidgetQueries.class);

	private final FeatureSettings featureSettings;
	private final String queryFolder;
	private final STGroup folder;

	/**
	 * Constructs the source system query configuration class, based on system
	 * settings.
	 *
	 * @param featureSettings
	 *            Feature collector system settings
	 */
	public FeatureWidgetQueries(FeatureSettings featureSettings) {
		this.featureSettings = featureSettings;
		this.queryFolder = this.featureSettings.getQueryFolder();
		this.folder = new STGroupDir(queryFolder, '$', '$');
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

	/**
	 * Retrieves source system history/trending queries based on the query name
	 * (without the file type) and other parameters.
	 *
	 * @param sprintStartDate
	 *            The sprint start data in ISO format.
	 * @param sprintEndDate
	 *            The sprint end data in ISO format.
	 * @param sprintDeltaDate
	 *            The delta date in ISO format.
	 * @param queryName
	 *            The source system query name (without the file type).
	 * @return A given historical source system query, in String format.
	 */
	public String getTrendingQuery(String sprintStartDate,
			String sprintEndDate, String sprintDeltaDate, String queryName) {
		ST st = folder.getInstanceOf(queryName);
		st.add("sprintStartDate", sprintStartDate);
		st.add("sprintEndDate", sprintEndDate);
		st.add("sprintDeltaDate", sprintDeltaDate);
		String query = st.render();

		return query;
	}
}
