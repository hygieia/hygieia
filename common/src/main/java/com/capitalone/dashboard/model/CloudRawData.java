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

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents an EC2 instance from AWS
 * 
 * Possible collectors: AWS, Microsoft Azure
 * 
 * @author cuo722 naa505
 * 
 */
@Document(collection = "rawCloudData")
public class CloudRawData extends BaseModel {
	private String instanceId;
	private int age;
	private boolean isEncrypted;
	private boolean isStopped;
	private boolean isTagged;
	private double cpuUtilization;
	private Date timestamp;
	private String accountName;
	
	public String getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public boolean isEncrypted() {
		return isEncrypted;
	}
	
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	
	public boolean isStopped() {
		return isStopped;
	}
	
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}
	
	public boolean isTagged() {
		return isTagged;
	}
	
	public void setTagged(boolean isTagged) {
		this.isTagged = isTagged;
	}
	
	public double getCpuUtilization() {
		return cpuUtilization;
	}
	
	public void setCpuUtilization(double cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
