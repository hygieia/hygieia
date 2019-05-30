package com.capitalone.dashboard.collector;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class SCMHttpRestClient {
  private static final Log LOG = LogFactory.getLog(SCMHttpRestClient.class);

  @Inject
  private GitSettings settings;

  @Inject
  private RestTemplate restTemplate;

  public ResponseEntity<String> makeRestCall(URI uri, String userId, String password) {
    String id = userId;
    String secret = password;
    if (LOG.isDebugEnabled()) {
      LOG.debug("GET " + uri);
    }
    // fallback to global credentials
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(password)) {
      id = settings.getUsername();
      secret = settings.getPassword();
    }
    // Basic Auth only.
    if (!"".equals(id) && !"".equals(secret)) {
      return restTemplate.exchange(
          uri, HttpMethod.GET, new HttpEntity<>(createHeaders(id, secret)), String.class);

    } else {
      return restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
    }
  }

    private HttpHeaders createHeaders(final String userId, final String password) {
        String auth = userId + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
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
