package com.capitalone.dashboard.collector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility to read/parse a json data from a file. This utility is only used within
 * unit tests.
 */
public class JsonUtilsTest {

    private static final Log LOG = LogFactory.getLog(JsonUtilsTest.class);

    public static String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultBitbucketServerClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }


    public static JSONObject getJsonObj(String fileName) throws IOException {
        try {
            return (JSONObject) new JSONParser().parse(getJson(fileName));
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }
}

/*
 * SPDX-Copyright: Copyright (c) Capital One Services, LLC
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 Capital One Services, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
