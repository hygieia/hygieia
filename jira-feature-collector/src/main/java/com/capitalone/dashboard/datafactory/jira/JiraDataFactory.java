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

package com.capitalone.dashboard.datafactory.jira;

import java.util.List;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.capitalone.dashboard.datafactory.DataFactory;

/**
 * Interface for Jira data connection. An implemented class should be able to create a formatted request,
 * and retrieve a response in JSON syntax from that request to Jira.
 *
 * @author KFK884
 *
 */
public interface JiraDataFactory extends DataFactory{
	String setQuery(String query);

	List<Issue> getJiraIssues();

	String getBasicQuery();

	int getPageIndex();

	void destroy();
}
