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
	public void updateObjectInformation();

	public String getLocalTimeStampFromUnix(long unixTimeStamp);

	public String getChangeDateMinutePrior(String changeDateISO);

	public String getSprintBeginDateFilter();

	public String getSprintEndDateFilter();

	public String getSprintDeltaDateFilter();

	public String getTodayDateISO();

	public void setTodayDateISO(String todayDateISO);
}
