package com.capitalone.dashboard.client;

/**
 * Implementable interface for super abstract class VersionOneInfo.
 *
 * @author kfk884
 *
 */
public interface DataClientSetup {
	/**
	 * This method is used to update the MongoDB database with model defined from VersionOne.
	 * This both updates MongoDB and performs the call to VersionOne.
	 *
	 * @see Story
	 */
	void updateObjectInformation();

	String getLocalTimeStampFromUnix(long unixTimeStamp);

	String getChangeDateMinutePrior(String changeDateISO);

	String getSprintBeginDateFilter();

	String getSprintEndDateFilter();

	String getSprintDeltaDateFilter();

	String getTodayDateISO();

	void setTodayDateISO(String todayDateISO);
}
