package com.capitalone.dashboard.gitlab;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.GitRequest;

@Component
public class GitlabIssuesResponseMapper {

	private GitlabUrlUtility gitlabUrlUtility;

	public void init(GitlabUrlUtility gitlabUrlUtility) {
		this.gitlabUrlUtility = gitlabUrlUtility;
	}

	public List<GitRequest> map(GitlabIssue[] gitlabIssues, String repoUrl) throws HygieiaException {
		String[] orgAndRepoName = null;

		List<GitRequest> issues = new ArrayList<>();
		for (GitlabIssue gitlabIssue : gitlabIssues) {
			GitRequest issue = map(repoUrl, gitlabIssue);
			if (null == orgAndRepoName) {
				orgAndRepoName = this.gitlabUrlUtility.getOrgAndRepoName(gitlabIssue.getWebUrl());
			}
			issue.setOrgName(orgAndRepoName[0]);
			issue.setRepoName(orgAndRepoName[1]);
			issues.add(issue);
		}

		return issues;
	}

	private GitRequest map(String repoUrl, GitlabIssue gitlabIssue) {
		String created = gitlabIssue.getCreatedAt();
		String closed = gitlabIssue.getClosedAt();
		long createdTimestamp = new DateTime(created).getMillis();

		GitRequest issue = new GitRequest();
		if (closed != null && closed.length() >= 10) {
			long mergedTimestamp = new DateTime(closed).getMillis();
			issue.setScmCommitTimestamp(mergedTimestamp);
			issue.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
		}
		issue.setUserId(gitlabIssue.getAuthorName());
		issue.setScmUrl(repoUrl);
		issue.setTimestamp(createdTimestamp);
		issue.setScmCommitLog(gitlabIssue.getTitle());
		issue.setCreatedAt(createdTimestamp);
		issue.setUpdatedAt(new DateTime(gitlabIssue.getUpdatedAt()).getMillis());
		issue.setClosedAt(new DateTime(closed).getMillis());
		issue.setNumber(gitlabIssue.getIid());
		issue.setRequestType("issue");
		if (closed != null) {
			issue.setState("closed");
		} else {
			issue.setState("open");
		}

		return issue;
	}

}
