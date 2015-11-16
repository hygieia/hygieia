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

package com.capitalone.dashboard.collector;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the Cloud collector.
 *
 * @author
 */
@Component
@ConfigurationProperties(prefix = "aws")
public class AWSCloudSettings {
	private String cron;
	private String proxyURL;
	private int proxyPort;
	private String proxyUser;
	private String proxyPassword;
	private List<String>  validTagKey;

	public List<String> getValidTagKey() {
		return validTagKey;
	}

	public void setValidTagKey(List<String> validTagKey) {
		this.validTagKey = validTagKey;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyURL() {
		return proxyURL;
	}

	public void setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
