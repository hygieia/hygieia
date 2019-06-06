package com.capitalone.dashboard.collector;

import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class PageMetadataTest {

    private PageMetadata testObject;


    @Test
    public void testLastPage() throws URISyntaxException, IOException {

        URIBuilder uriBuilder = new URIBuilder("http://company.com/xyz?additonalParam1=1234&additonalParam2=1234");

        JSONObject jsonObject = JsonUtilsTest.getJsonObj("/bitbucket-server/test-pagination-is-last-page.json");

        JSONArray jsonArray = (JSONArray) jsonObject.get("values");

        testObject = new PageMetadata(uriBuilder.build(), jsonObject, jsonArray);

        assertNull(testObject.getNextPageUrl());

        assertTrue(testObject.isLastPage());

    }

    @Test
    public void testIsNotLastPage() throws URISyntaxException, IOException {

        URIBuilder uriBuilder = new URIBuilder("http://company.com/xyz?additonalParam1=1234&additonalParam2=1234");

        JSONObject jsonObject = JsonUtilsTest.getJsonObj("/bitbucket-server/test-pagination-not-last-page.json");

        JSONArray jsonArray = (JSONArray) jsonObject.get("values");

        testObject = new PageMetadata(uriBuilder.build(), jsonObject, jsonArray);

        assertFalse(testObject.isLastPage());

        assertNotNull(testObject.getNextPageUrl());

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
