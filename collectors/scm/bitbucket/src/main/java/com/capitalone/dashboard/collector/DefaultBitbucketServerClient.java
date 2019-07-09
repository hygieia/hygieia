package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.bitbucketapi.BitbucketApiUrlBuilder;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.capitalone.dashboard.collector.JSONParserUtils.str;

/**
 * Implementation of a git client to connect to an Atlassian Bitbucket <i>Server</i> product.
 * <p>
 * Note about naming scheme: Atlassian has two different Bitbucket products that use different
 * rest API's: Bitbucket <i>Cloud</i> (formerly known as Bitbucket) and Bitbucket <i>Server</i> (formerly known as Stash).
 * <p>
 * Rest API's:
 * <ul>
 * <li><b>Bitbucket Cloud:</b> https://confluence.atlassian.com/bitbucket/version-2-423626329.html</li>
 * <li><b>Bitbucket Server:</b> https://developer.atlassian.com/static/rest/stash/3.11.3/stash-rest.html</li>
 * </ul>
 * <b>
 *
 * @see <a href="https://confluence.atlassian.com/bitbucketserver/bitbucket-rebrand-faq-779298912.html">Bitbucket rebrand FAQ</a>
 * @see <a href="https://github.com/capitalone/Hygieia/issues/609">Confusion on Stash/Bitbucket implementations #609</a>
 */
@Component("bitbucket-server")
@ConditionalOnProperty(prefix = "git", name = "product", havingValue = "server")
public class DefaultBitbucketServerClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultBitbucketServerClient.class);

    private final GitSettings settings;

    private final BitbucketApiUrlBuilder bitbucketApiUrlBuilder;

    private final SCMHttpRestClient scmHttpRestClient;

    @Autowired
    public DefaultBitbucketServerClient(GitSettings settings, BitbucketApiUrlBuilder bitbucketApiUrlBuilder, SCMHttpRestClient scmHttpRestClient) {
        this.settings = settings;
        this.bitbucketApiUrlBuilder = bitbucketApiUrlBuilder;
        this.scmHttpRestClient=scmHttpRestClient;
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public List<Commit> getCommits(GitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();
        URI queryUriPage = null;

        try {

            URI queryUri = buildUri((String) repo.getOptions().get("url"), repo.getBranch(), repo.getLastUpdateCommit());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rest Url: " + queryUri);
            }

            // decrypt password
            String decryptedPassword = "";
            if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
                try {
                    decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
                } catch (EncryptionException e) {
                    LOG.error(e.getMessage());
                }
            }

            boolean lastPage = false;
            queryUriPage = queryUri;
            while (!lastPage) {
                ResponseEntity<String> response = scmHttpRestClient.makeRestCall(queryUriPage, repo.getUserId(), decryptedPassword);
                JSONObject jsonParentObject = JSONParserUtils.parseAsObject(response);
                JSONArray jsonArray = (JSONArray) jsonParentObject.get("values");

                for (Object item : jsonArray) {
                    JSONObject jsonObject = (JSONObject) item;
                    String sha = str(jsonObject, "id");
                    JSONObject authorObject = (JSONObject) jsonObject.get("author");
                    String message = str(jsonObject, "message");
                    String author = str(authorObject, "name");
                    long timestamp = Long.valueOf(str(jsonObject, "authorTimestamp"));
                    JSONArray parents = (JSONArray) jsonObject.get("parents");
                    List<String> parentShas = new ArrayList<>();
                    if (parents != null) {
                        for (Object parentObj : parents) {
                            parentShas.add(str((JSONObject) parentObj, "id"));
                        }
                    }

                    Commit commit = new Commit();
                    commit.setTimestamp(System.currentTimeMillis());
                    commit.setScmUrl(repo.getRepoUrl());
                    commit.setScmBranch(repo.getBranch());
                    commit.setScmRevisionNumber(sha);
                    commit.setScmParentRevisionNumbers(parentShas);
                    commit.setScmAuthor(author);
                    commit.setScmCommitLog(message);
                    commit.setScmCommitTimestamp(timestamp);
                    commit.setType(parentShas.size() > 1 ? CommitType.Merge : CommitType.New);
                    commit.setNumberOfChanges(1);
                    commits.add(commit);
                }

                PageMetadata pageMetadata = new PageMetadata(queryUri, jsonParentObject, jsonArray);
                lastPage = pageMetadata.isLastPage();
                queryUriPage = pageMetadata.getNextPageUrl();
            }

            repo.setLastUpdated(System.currentTimeMillis());
        } catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (RestClientException re) {
            LOG.error("Failed to obtain commits from " + queryUriPage, re);
        }

        return commits;
    }

    // package for junit
    @SuppressWarnings({"PMD.NPathComplexity"})
    /*package*/ URI buildUri(final String rawUrl, final String branch, final String lastKnownCommit) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(bitbucketApiUrlBuilder.buildReposApiUrl(rawUrl));
        URIBuilder uriBuilder = builder.setPath(builder.getPath() + "/commits");

        if (branch == null || branch.length() == 0) {
            uriBuilder.addParameter("until", "master");
        } else {
            String branchRef = "refs/heads/" + branch;
            uriBuilder.addParameter("until", branchRef.replaceAll(" ", "%20"));
        }

        if (lastKnownCommit != null && lastKnownCommit.length() > 0) {
            uriBuilder.addParameter("since", lastKnownCommit);
        }

        if (settings.getPageSize() > 0) {
            uriBuilder.addParameter("limit", String.valueOf(settings.getPageSize()));
        }

        return uriBuilder.build();

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
