package com.capitalone.dashboard.dao;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class PullRequestDao {

    private static final Log LOG = LogFactory.getLog(PullRequestDao.class);

    @Inject
    private GitRequestRepository gitRequestRepository;

    @Inject
    private CommitRepository commitRepository;

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
}

/*
 * Copyright 2019 Pandora Media, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See accompanying LICENSE file or you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


