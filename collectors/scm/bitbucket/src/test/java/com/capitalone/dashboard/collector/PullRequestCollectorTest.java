package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.bitbucketapi.BitbucketApiUrlBuilder;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;


@RunWith(MockitoJUnitRunner.class)
public class PullRequestCollectorTest {

    @Mock
    private BitbucketApiUrlBuilder bitbucketApiUrlBuilder;

    @Mock
    private SCMHttpRestClient scmHttpRestClient;

    @Mock
    private GitSettings settings;

    @Mock
    private RestOperations rest;

    @Mock
    private GitRequestRepository gitRequestRepository;

    @InjectMocks
    private PullRequestCollector pullRequestCollector;


    @Test
    public void testGetPullRequests() throws IOException, URISyntaxException, EncryptionException {
        given(settings.getKey()).willReturn("abcdefghijklmnopqrstuvwxyz1234567");
        String encPassword = Encryption.encryptString("password", settings.getKey());
        given(settings.getPassword()).willReturn(encPassword);
        // given
        String prResponseTestData = getJson("/bitbucket-server/pr-response-test-data-1.json");
        GitRepo repo = new GitRepo();
        repo.setUserId("user");
        repo.setPassword(encPassword);
        String repoUrl = "https://username@company.com/scm/myproject/myrepository.git";
        repo.setRepoUrl(repoUrl);
        repo.getOptions().put("url", repoUrl);
        repo.setBranch("master");
        URI uri1 =
                URI.create(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests?at=refs%2Fheads%2Fmaster&state=OPEN&limit=0");
        URI uri2 =
                URI.create(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests?at=refs%2Fheads%2Fmaster&state=OPEN&limit=0");

        given(
                rest.exchange(
                        eq(uri1), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));
        given(
                rest.exchange(
                        eq(uri2), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));

        URI value = new URI("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository");
        given(bitbucketApiUrlBuilder.buildReposApiUrl(repoUrl)).willReturn(value);
        value =
                new URI(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests");
        given(bitbucketApiUrlBuilder.buildPullRequestApiUrl(repoUrl)).willReturn(value);

        given(scmHttpRestClient.makeRestCall(uri1, repo.getUserId(), "password"))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));
        given(scmHttpRestClient.makeRestCall(uri2, null, ""))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));


        // when
        int pullCount = pullRequestCollector.getPullRequests(repo, "OPEN");

        // then
        assertEquals(pullCount, 3);

    }

    @Test
    public void testGetPullRequests_RecentlyUpdated() throws IOException, URISyntaxException, EncryptionException {
        given(settings.getKey()).willReturn("abcdefghijklmnopqrstuvwxyz1234567");
        String encPassword = Encryption.encryptString("password", settings.getKey());
        given(settings.getPassword()).willReturn(encPassword);
        // given
        String prResponseTestData = getJson("/bitbucket-server/pr-response-test-data-1.json");
        GitRepo repo = new GitRepo();
        repo.setUserId("user");
        repo.setPassword(encPassword);
        String repoUrl = "https://username@company.com/scm/myproject/myrepository.git";
        repo.setRepoUrl(repoUrl);
        repo.getOptions().put("url", repoUrl);
        repo.setBranch("master");
        URI uri1 =
                URI.create(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests?at=refs%2Fheads%2Fmaster&state=OPEN&limit=0");
        URI uri2 =
                URI.create(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests?at=refs%2Fheads%2Fmaster&state=OPEN&limit=0");

        given(
                rest.exchange(
                        eq(uri1), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));
        given(
                rest.exchange(
                        eq(uri2), eq(HttpMethod.GET), Matchers.any(HttpEntity.class), eq(String.class)))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));

        URI value = new URI("https://company.com/rest/api/1.0/projects/myproject/repos/myrepository");
        given(bitbucketApiUrlBuilder.buildReposApiUrl(repoUrl)).willReturn(value);
        value =
                new URI(
                        "https://company.com/rest/api/1.0/projects/myproject/repos/myrepository/pull-requests");
        given(bitbucketApiUrlBuilder.buildPullRequestApiUrl(repoUrl)).willReturn(value);

        given(scmHttpRestClient.makeRestCall(uri1, repo.getUserId(), "password"))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));
        given(scmHttpRestClient.makeRestCall(uri2, null, ""))
                .willReturn(new ResponseEntity<>(prResponseTestData, HttpStatus.OK));

        GitRequest existingPR = new GitRequest();
        existingPR.setUpdatedAt(1538071463469L);
        given(gitRequestRepository.findByCollectorItemIdAndNumberAndRequestType(
                repo.getId(), "7439", "pull")).willReturn(existingPR);
        // when
        int pullCount = pullRequestCollector.getPullRequests(repo, "OPEN");

        // then
        assertEquals(pullCount, 2);

    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = PullRequestCollectorTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
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
