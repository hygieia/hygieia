package com.capitalone.dashboard.gitlab;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.gitlab.model.GitlabCommitStatus;
import com.capitalone.dashboard.gitlab.model.GitlabNote;
import com.capitalone.dashboard.gitlab.model.GitlabRequest;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.CommitStatus;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.Review;

@Component
public class GitlabRequestsResponseMapper {
	private static final Log LOG = LogFactory.getLog(GitlabRequestsResponseMapper.class);

	private RestOperations restOperations;
	private GitlabUrlUtility gitlabUrlUtility;
	private String apiToken;

	public void init(GitlabUrlUtility gitlabUrlUtility, RestOperations restOperations) {
		this.gitlabUrlUtility = gitlabUrlUtility;
		this.restOperations = restOperations;
	}

	public List<GitRequest> map(GitlabRequest[] gitlabRequests, String repoUrl, String branch, String apiToken,
			Map<Long, String> mrCloseMap) throws HygieiaException {
		List<GitRequest> requests = new ArrayList<GitRequest>();

		String targetBranch = branch;
		if (StringUtils.isBlank(targetBranch)) {
			targetBranch = "master";
		}

		this.apiToken = apiToken;

		String[] orgAndRepoName = null;

		for (GitlabRequest gitlabRequest : gitlabRequests) {
			LOG.debug("targetBranch: " + targetBranch);
			LOG.debug("gitlabRequest.getTargetBranch(): " + gitlabRequest.getTargetBranch());

			if (!StringUtils.equals(targetBranch, gitlabRequest.getTargetBranch())) continue;

			GitRequest request = map(repoUrl, targetBranch, gitlabRequest);
			if (null == orgAndRepoName) {
				orgAndRepoName = this.gitlabUrlUtility.getOrgAndRepoName(gitlabRequest.getWebUrl());
			}
			request.setOrgName(orgAndRepoName[0]);
			request.setRepoName(orgAndRepoName[1]);
			requests.add(request);

			// new GitRequest, never fetched!
			if (MapUtils.isEmpty(mrCloseMap) || mrCloseMap.get(request.getUpdatedAt()) == null) continue;

			// the fetched GitRequest reached!
			if (mrCloseMap.get(request.getUpdatedAt()).equals(request.getNumber())) break;
		}

		return requests;
	}

	private GitRequest map(String repoUrl, String branch, GitlabRequest gitlabRequest) throws HygieiaException {
		GitRequest request = new GitRequest();

		request.setState("open");

		String merged = null;
		String closed = null;
		if ("merged".equals(gitlabRequest.getState())) {
			merged = gitlabRequest.getUpdatedAt();
			request.setState("merged");

		} else if ("closed".equals(gitlabRequest.getState())) {
			closed = gitlabRequest.getUpdatedAt();
			request.setState("closed");
		}

		long createdTimestamp = new DateTime(gitlabRequest.getCreatedAt()).getMillis();

		if (merged != null && merged.length() >= 10) {
			long mergedTimestamp = new DateTime(merged).getMillis();
			request.setScmCommitTimestamp(mergedTimestamp);
			request.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
			request.setMergedAt(new DateTime(merged).getMillis());
		}

		if (closed != null && closed.length() >= 10) {
			request.setClosedAt(new DateTime(closed).getMillis());
		}

		request.setUserId(gitlabRequest.getAuthorName());
		request.setScmUrl(repoUrl);
		request.setScmBranch(branch);
		request.setTimestamp(createdTimestamp);
		request.setScmRevisionNumber(gitlabRequest.getSha());
		request.setScmCommitLog(gitlabRequest.getTitle());
		request.setCreatedAt(createdTimestamp);
		request.setUpdatedAt(new DateTime(gitlabRequest.getUpdatedAt()).getMillis());
		request.setNumber(gitlabRequest.getIid());
		request.setRequestType("pull");
		request.setSourceBranch(gitlabRequest.getSourceBranch());
		request.setTargetBranch(gitlabRequest.getTargetBranch());

		List<Review> reviews = new ArrayList<>();
		List<Comment> comments = getCommentsAndReviews(gitlabRequest.getWebUrl(), gitlabRequest, reviews);
		request.setComments(comments);
		request.setReviews(reviews);
		request.setCommentsUrl(gitlabRequest.getWebUrl());
		request.setReviewCommentsUrl(gitlabRequest.getWebUrl());

		if (StringUtils.isNotBlank(gitlabRequest.getSha())) {
			List<CommitStatus> commitStatuses = getCommitStatuses(gitlabRequest.getWebUrl(), branch,
					gitlabRequest.getSha());
			request.setCommitStatuses(commitStatuses);
		}

		return request;
	}

	private List<Comment> getCommentsAndReviews(String webUrl, GitlabRequest gitlabRequest, List<Review> reviews) {
		List<Comment> comments = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildMergeRequestNotesApiUrl(webUrl, gitlabRequest.getIid(), GitlabUrlUtility.RESULTS_PER_PAGE);

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			int pageOfRequestsSize = 0;
			HttpHeaders headers = new HttpHeaders();
			headers.add("PRIVATE-TOKEN", apiToken);
			ResponseEntity<GitlabNote[]> response = restOperations.exchange(apiUrl, HttpMethod.GET,
					new HttpEntity<>(headers), GitlabNote[].class);
			GitlabNote[] gitlabNotes = response.getBody();
			for (GitlabNote gitlabNote : gitlabNotes) {
				Comment comment = new Comment();
				comment.setUser(gitlabNote.getAuthorName());
				comment.setCreatedAt(new DateTime(gitlabNote.getCreatedAt()).getMillis());
				comment.setUpdatedAt(new DateTime(gitlabNote.getUpdatedAt()).getMillis());
				comment.setBody(gitlabNote.getBody());
				comments.add(comment);

				if (!gitlabNote.isSystem()) {
					Review review = new Review();
					review.setState("pending");
					if (gitlabNote.isResolvable()) {
						review.setState("approved");
					}
					review.setBody(gitlabNote.getBody());
					reviews.add(review);
				}

				pageOfRequestsSize++;
			}
			if (pageOfRequestsSize < GitlabUrlUtility.RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

		return comments;
	}

	private List<CommitStatus> getCommitStatuses(String webUrl, String branch, String commitSha) {
		Map<String, CommitStatus> statuses = new HashMap<>();

		URI apiUrl = gitlabUrlUtility.buildCommitStatusesApiUrl(webUrl, branch, commitSha, GitlabUrlUtility.RESULTS_PER_PAGE);

		boolean hasMorePages = true;
		int nextPage = 1;
		while (hasMorePages) {
			int pageOfRequestsSize = 0;
			HttpHeaders headers = new HttpHeaders();
			headers.add("PRIVATE-TOKEN", apiToken);
			ResponseEntity<GitlabCommitStatus[]> response = restOperations.exchange(apiUrl, HttpMethod.GET,
					new HttpEntity<>(headers), GitlabCommitStatus[].class);
			GitlabCommitStatus[] gitlabCommitStatuses = response.getBody();
			for (GitlabCommitStatus gitlabCommitStatus : gitlabCommitStatuses) {
				String context = gitlabCommitStatus.getName();
				if ((context != null) && !statuses.containsKey(context)) {
					CommitStatus status = new CommitStatus();
					status.setContext(context);
					status.setDescription(gitlabCommitStatus.getDescription());
					status.setState(gitlabCommitStatus.getStatus());
					statuses.put(context, status);
				}
				pageOfRequestsSize++;
			}
			if (pageOfRequestsSize < GitlabUrlUtility.RESULTS_PER_PAGE) {
				hasMorePages = false;
				continue;
			}
			apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
			nextPage++;
		}

		return new ArrayList<>(statuses.values());
	}

}
