package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.bitbucketapi.BitbucketApiUrlBuilder;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.capitalone.dashboard.collector.JSONParserUtils.parseAsObject;


@Component
public class PullRequestCollector {

    private static final Log LOG = LogFactory.getLog(PullRequestCollector.class);
    private static final String PULL = "pull";
    private static final String OPEN = "open";
    private static final String CLOSED = "closed";
    private static final String MERGED = "merged";

    @Inject
    private GitSettings settings;

    @Inject
    private BitbucketApiUrlBuilder bitbucketApiUrlBuilder;

    @Inject
    private SCMHttpRestClient scmHttpRestClient;

    @Inject
    private GitRequestRepository gitRequestRepository;

    @Inject
    private CommitRepository commitRepository;


    /** This method fetches pull-request using Bitbucket REST APIs and stores them to Hygieia DB.
     * We can stop this processing as soon as we find a PR which has not changed(updateAt is same in Hygieia DB and REST response).
     * @param repo              Bitbucket Repo object
     * @param status            open/merged
     *
     * @return
     * @throws EncryptionException
     */
    public int getPullRequests(GitRepo repo, String status) {
        String decryptedPassword;
        try {
            decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
        } catch (EncryptionException e) {
            throw new RuntimeException("Unable to decrypt SCM credentials", e);
        }
        String branch = (repo.getBranch() != null) ? repo.getBranch() : "master";
        List<GitRequest> pulls;

        URI pageUrl;
        int pullCount = 0;
        try {
            URI uri = bitbucketApiUrlBuilder.buildPullRequestApiUrl(repo.getRepoUrl());
            String branchId = "refs/heads/" + branch;
            pageUrl =
                    new URIBuilder(uri)
                            .addParameter("at", branchId)
                            .addParameter("state", status)
                            .addParameter("limit", String.valueOf(settings.getPageSize()))
                            .build();

            boolean lastPage = false;
            boolean stop = false;
            URI queryUrlPage = pageUrl;

            while (!lastPage && !stop) {
                LOG.info("Executing [" + queryUrlPage);
                pulls = new ArrayList<>();
                ResponseEntity<String> response =
                        scmHttpRestClient.makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword);
                JSONObject jsonArray = parseAsObject(response);
                JSONArray values = (JSONArray) jsonArray.get("values");
                for (Object item : values) {
                    JSONObject jsonObject = (JSONObject) item;
                    Long updatedAt = (Long) jsonObject.get("updatedDate");


                    GitRequest pull = getPullRequest(repo, jsonObject);
                    GitRequest existingPull =
                            gitRequestRepository.findByCollectorItemIdAndNumberAndRequestType(
                                    repo.getId(), pull.getNumber(), "pull");

                    if (existingPull != null) {
                        stop = (Long.valueOf(existingPull.getUpdatedAt()).compareTo(updatedAt) == 0);
                        if (stop) {//Found a match for last updated PR so stop
                            break;
                        }
                    }

                    populatePullRequestMergeCommit(repo, pull);
                    pulls.add(pull);
                }
                try {
                    pullCount += processList(repo, pulls, "pull");
                } catch (Exception ex) {
                    LOG.error("failed to process Pull Requests", ex);
                    throw new RuntimeException("Unable to process pull requests",ex);
                }

                PageMetadata pageMetadata = new PageMetadata(pageUrl, jsonArray, values);
                lastPage = pageMetadata.isLastPage();
                queryUrlPage = pageMetadata.getNextPageUrl();
            }
        } catch (URISyntaxException e) {
            LOG.error("Unable to construct Bitbucket API URL" + e.getMessage());
        }
        return pullCount;
    }

    /**
     * Pull Requests merge commits can change after final merge to a branch. This depends on the merge
     * strategy used : merge(no fast forward),squash,rebase or fast-forward
     *
     * @param repo
     * @param pull
     */
    private void populatePullRequestMergeCommit(GitRepo repo, GitRequest pull) {
        if (MERGED.equals(pull.getState())) {
            String decryptedPassword =
                    null;
            try {
                decryptedPassword = Encryption.decryptString(repo.getPassword(), settings.getKey());
            } catch (EncryptionException e) {
                throw new RuntimeException("Unable to decrypt SCM credentials", e);
            }
            URI pageUrl;
            try {
                URI uri =
                        bitbucketApiUrlBuilder.buildPullRequestActivitiesApiUrl(
                                repo.getRepoUrl(), pull.getNumber());
                pageUrl =
                        new URIBuilder(uri)
                                .addParameter("limit", String.valueOf(settings.getPageSize()))
                                .build();
                boolean lastPage = false;
                boolean stop = false;
                URI queryUrlPage = pageUrl;
                while (!lastPage && !stop) {
                    LOG.info("Executing [" + queryUrlPage);
                    ResponseEntity<String> response =
                            scmHttpRestClient.makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword);
                    JSONObject jsonArray = parseAsObject(response);
                    JSONArray values = (JSONArray) jsonArray.get("values");
                    for (Object item : values) {
                        JSONObject jsonObject = (JSONObject) item;
                        String actionType = (String) jsonObject.get("action");
                        stop = "MERGED".equals(actionType);
                        if (stop) {
                            populateScmRevisionNumber(pull, jsonObject);
                            break;
                        }
                    }
                    PageMetadata pageMetadata = new PageMetadata(pageUrl, jsonArray, values);
                    lastPage = pageMetadata.isLastPage();
                    queryUrlPage = pageMetadata.getNextPageUrl();
                }
            } catch (URISyntaxException e) {
                LOG.error("Unable to construct Bitbucket API URL" + e.getMessage());
            }
        }
    }
    public int processList(GitRepo repo, List<GitRequest> entries, String type) {
        int count = 0;
        if (CollectionUtils.isEmpty(entries)) return 0;

        for (GitRequest entry : entries) {
            LOG.debug(entry.getTimestamp() + ":::" + entry.getScmCommitLog());
            GitRequest existing =
                    gitRequestRepository.findByCollectorItemIdAndNumberAndRequestType(
                            repo.getId(), entry.getNumber(), type);

            if (existing == null) {
                entry.setCollectorItemId(repo.getId());
                count++;
            } else {
                entry.setId(existing.getId());
                entry.setCollectorItemId(repo.getId());
            }
            gitRequestRepository.save(entry);

            // fix merge commit type for squash merged and rebased merged PRs
            // PRs that were squash merged or rebase merged have only one parent
            if ("pull".equalsIgnoreCase(type) && "merged".equalsIgnoreCase(entry.getState())) {
                List<Commit> commits =
                        commitRepository.findByScmRevisionNumber(entry.getScmRevisionNumber());
                for (Commit commit : commits) {
                    if (commit.getType() != null) {
                        if (commit.getType() != CommitType.Merge) {
                            commit.setType(CommitType.Merge);
                            commitRepository.save(commit);
                        }
                    } else {
                        commit.setType(CommitType.Merge);
                        commitRepository.save(commit);
                    }
                }
            }
        }
        return count;
    }
    private void populateScmRevisionNumber(GitRequest pull, JSONObject jsonObject) {
        JSONObject commit = (JSONObject) jsonObject.get("commit");
        if (commit != null && commit.get("id") != null) {
            pull.setScmRevisionNumber((String) commit.get("id"));
        }
    }

    private GitRequest getPullRequest(
            GitRepo repo, JSONObject jsonObject) {
        String prNumber = jsonObject.get("id").toString();
        String message = (String) jsonObject.get("title");
        JSONObject fromRef = (JSONObject) jsonObject.get("fromRef");
        String sha = (String) fromRef.get("latestCommit");
        Long createdAt = (Long) jsonObject.get("createdDate");
        Long updatedAt = (Long) jsonObject.get("updatedDate");
        GitRequest pull = new GitRequest();
        pull.setScmCommitLog(message);
        pull.setNumber(prNumber);
        pull.setScmUrl(repo.getRepoUrl());
        pull.setScmRevisionNumber(sha);
        long currentTimeMillis = new DateTime(createdAt).getMillis();
        pull.setCreatedAt(currentTimeMillis);
        pull.setTimestamp(currentTimeMillis);
        pull.setUpdatedAt(new DateTime(updatedAt).getMillis());
        pull.setUserId(getPullRequestAuthor(jsonObject));
        pull.setRequestType(PULL);
        pull.setScmBranch(repo.getBranch());

        String state = (String) jsonObject.get("state");
        if (StringUtils.equals("OPEN", state)) {
            pull.setState(OPEN);
        } else if (StringUtils.equals("DECLINED", state)) {
            pull.setState(CLOSED);
        } else if (StringUtils.equals("MERGED", state)) {
            pull.setState(MERGED);
        }

        return pull;
    }

    private String getPullRequestAuthor(JSONObject jsonObject) {
        JSONObject author = (JSONObject) jsonObject.get("author");
        JSONObject user = (JSONObject) author.get("user");
        return user.get("name").toString();
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
