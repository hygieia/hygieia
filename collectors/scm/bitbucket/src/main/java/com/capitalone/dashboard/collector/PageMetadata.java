package com.capitalone.dashboard.collector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * This class represents metadata of a paginated response. It nicely encapsulates Page Metadata by keeping track of last
 * page and next page url.
 */
public class PageMetadata {

    private URI nextPageUrl;
    private boolean lastPage;
    private final URI currentPageUrl;
    private final JSONObject jsonArray;
    private final JSONArray values;

    public PageMetadata(URI currentPageUrl, JSONObject jsonArray, JSONArray values) {
        this.currentPageUrl = currentPageUrl;
        this.jsonArray = jsonArray;
        this.values = values;
        init();
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public URI getNextPageUrl() {
        return nextPageUrl;
    }

    private void init() {
        if (CollectionUtils.isEmpty(this.values)) {
            this.lastPage = true;
        } else {
            String isLastPage = str(this.jsonArray, "isLastPage");
            this.lastPage = isLastPage == null || Boolean.valueOf(isLastPage);
            String nextPageStart = str(this.jsonArray, "nextPageStart");
            if (nextPageStart != null && !"null".equals(nextPageStart)) {
                try {
                    this.nextPageUrl = new URIBuilder(this.currentPageUrl)
                            .addParameter("start", nextPageStart).build();
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Unable to create next page URI", e);
                }
            }
        }
    }

    private static String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
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
