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
