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

package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a feature (story/requirement) of a component.
 *
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 *
 * @author kfk884
 *
 */
@Document(collection = "cloud")
public class CloudCpuUtilization extends BaseModel {
	private int low;
	private int mid;
	private int high;

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}
}
