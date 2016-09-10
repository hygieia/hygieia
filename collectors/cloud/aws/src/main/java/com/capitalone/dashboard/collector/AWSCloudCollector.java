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

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 * 
 * @author KFK884
 */
public class AWSCloudCollector extends Collector {
	/**
	 * Creates a static prototype of the Cloud Collector, which includes any
	 * specific settings or configuration required for the use of this
	 * collector, including settings for connecting to any source systems.
	 * 
	 * @return A configured Cloud Collector prototype
	 */
	public static AWSCloudCollector prototype() {
		AWSCloudCollector protoType = new AWSCloudCollector();
		protoType.setName("AWSCloud");
		protoType.setOnline(true);
        protoType.setEnabled(true);
		protoType.setCollectorType(CollectorType.Cloud);
		protoType.setLastExecuted(System.currentTimeMillis());

		return protoType;
	}
}
