package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Comment;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGitHubClient implements GitHubClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

	private final GitHubSettings settings;

	private final RestOperations restOperations;
	private static final String SEGMENT_API = "/api/v3/repos/";
	private static final String PUBLIC_GITHUB_REPO_HOST = "api.github.com/repos/";
	private static final String PUBLIC_GITHUB_HOST_NAME = "github.com";
	private static final int FIRST_RUN_HISTORY_DEFAULT = 14;
	private static final int ORG_POS_IN_API_URL = 6;
	private static final int REPO_POS_IN_API_URL = 7;

    @Autowired
    public DefaultGitHubClient(GitHubSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"}) // agreed, fixme
    public List<Commit> getCommits(GitHubRepo repo, boolean firstRun) throws RestClientException {

        List<Commit> commits = new ArrayList<>();

        // format URL
        String repoUrl = (String) repo.getOptions().get("url");
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
        }
        URL url;
        String hostName = "";
        String protocol = "";
        try {
            url = new URL(repoUrl);
            hostName = url.getHost();
            protocol = url.getProtocol();
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage());
			throw new RestClientException(e.getMessage(), e);
        }
        String hostUrl = protocol + "://" + hostName + "/";
        String repoName = repoUrl.substring(hostUrl.length(), repoUrl.length());
        String apiUrl;
        if (hostName.startsWith(PUBLIC_GITHUB_HOST_NAME)) {
            apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + repoName;
        } else {
            apiUrl = protocol + "://" + hostName + SEGMENT_API + repoName;
            LOG.debug("API URL IS:" + apiUrl);
        }
        Date dt;
        if (firstRun) {
            int firstRunDaysHistory = settings.getFirstRunHistoryDays();
            if (firstRunDaysHistory > 0) {
                dt = getDate(new Date(), -firstRunDaysHistory, 0);
            } else {
                dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
            }
        } else {
            dt = getDate(new Date(repo.getLastUpdated()), 0, -10);
        }
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(dt);
        String thisMoment = String.format("%tFT%<tRZ", cal);

        String queryUrl = apiUrl.concat("/commits?sha=" + repo.getBranch()
                + "&since=" + thisMoment);
        /*
		 * Calendar cal = Calendar.getInstance(); cal.setTime(dateInstance);
		 * cal.add(Calendar.DATE, -30); Date dateBefore30Days = cal.getTime();
		 */

        // decrypt password
        String decryptedPassword = "";
        if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
            try {
                decryptedPassword = Encryption.decryptString(
                        repo.getPassword(), settings.getKey());
            } catch (EncryptionException e) {
                LOG.error(e.getMessage());
            }
        }
        boolean lastPage = false;
        int pageNumber = 1;
        String queryUrlPage = queryUrl;
        while (!lastPage) {
            ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword);
            JSONArray jsonArray = paresAsArray(response);
            for (Object item : jsonArray) {
                JSONObject jsonObject = (JSONObject) item;
                String sha = str(jsonObject, "sha");
                JSONObject commitObject = (JSONObject) jsonObject.get("commit");
                JSONObject commitAuthorObject = (JSONObject) commitObject.get("author");
                String message = str(commitObject, "message");
                String author = str(commitAuthorObject, "name");
                long timestamp = new DateTime(str(commitAuthorObject, "date"))
                        .getMillis();
				JSONObject authorObject = (JSONObject) jsonObject.get("author");
				String authorLogin = "";
				if (authorObject != null) {
					authorLogin = str(authorObject, "login");
				}
                JSONArray parents = (JSONArray) jsonObject.get("parents");
                List<String> parentShas = new ArrayList<>();
                if (parents != null) {
                    for (Object parentObj : parents) {
                        parentShas.add(str((JSONObject) parentObj, "sha"));
                    }
                }

                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(repo.getRepoUrl());
                commit.setScmBranch(repo.getBranch());
                commit.setScmRevisionNumber(sha);
                commit.setScmParentRevisionNumbers(parentShas);
                commit.setScmAuthor(author);
				commit.setScmAuthorLogin(authorLogin);
                commit.setScmCommitLog(message);
                commit.setScmCommitTimestamp(timestamp);
                commit.setNumberOfChanges(1);
                commit.setType(getCommitType(CollectionUtils.size(parents), message));
                commits.add(commit);
            }
            if (CollectionUtils.isEmpty(jsonArray)) {
                lastPage = true;
            } else {
                lastPage = isThisLastPage(response);
                pageNumber++;
                queryUrlPage = queryUrl + "&page=" + pageNumber;
            }
        }
        return commits;
    }
  
    private CommitType getCommitType(int parentSize, String commitMessage) {
        if (parentSize > 1) return CommitType.Merge;
        if (settings.getNotBuiltCommits() == null) return CommitType.New;
        for (String s : settings.getNotBuiltCommits()) {
            if (commitMessage.contains(s)) {
                return CommitType.NotBuilt;
            }
        }
        return CommitType.New;
    }

    private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInstance);
        cal.add(Calendar.DATE, offsetDays);
        cal.add(Calendar.MINUTE, offsetMinutes);
        return cal.getTime();
    }

    private boolean isThisLastPage(ResponseEntity<String> response) {
        HttpHeaders header = response.getHeaders();
        List<String> link = header.get("Link");
        if (link == null || link.isEmpty()) {
            return true;
        } else {
            for (String l : link) {
                if (l.contains("rel=\"next\"")) {
                    return false;
                }

            }
        }
        return true;
    }

    private ResponseEntity<String> makeRestCall(String url, String userId,
                                                String password) {
        // Basic Auth only.
        if (!"".equals(userId) && !"".equals(password)) {
            return restOperations.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userId, password)),
                    String.class);

        } else {
            return restOperations.exchange(url, HttpMethod.GET, null,
                    String.class);
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

    private JSONArray paresAsArray(ResponseEntity<String> response) {
        try {
            return (JSONArray) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONArray();
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

	@Override
	@SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength", "PMD.NcssMethodCount"}) // agreed, fixme
	public List<GitRequest> getPulls(GitHubRepo repo, boolean firstRun, GitRequestRepository gitRequestRepository) {

		List<GitRequest> pulls = new ArrayList<>();

		// format URL
		String repoUrl = (String) repo.getOptions().get("url");
		if (repoUrl.endsWith(".git"))
			repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));

		URL url;
		String hostName = "";
		String protocol = "";
		try {
			url = new URL(repoUrl);
			hostName = url.getHost();
			protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}
		String hostUrl = protocol + "://" + hostName + "/";
		String repoName = repoUrl.substring(hostUrl.length(), repoUrl.length());
		String apiUrl;
		if (hostName.startsWith(PUBLIC_GITHUB_HOST_NAME)) {
			apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + repoName;
		} else {
			apiUrl = protocol + "://" + hostName + SEGMENT_API + repoName;
		}
		LOG.debug("API URL IS:"+apiUrl);

		String branch = "master";
		if (repo.getBranch() != null)
			branch = repo.getBranch();
		String pageUrl = apiUrl.concat("/pulls?state=all&base="+branch);

		// decrypt password
		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}

		try {
			ResponseEntity<String> response = makeRestCall(pageUrl, repo.getUserId(), decryptedPassword);

			HttpHeaders headers = response.getHeaders();
			List<String> pagevalues = headers.get("Link");
			int pageCount = 0;
			
			if (pagevalues == null) {
				pageCount = 1;
			} else {
				String[] splited1 = pagevalues.get(0).split(";");
				String[] splited2 = splited1[1].split("=");
				String[] splitted3 = splited2[4].split(">");
				String lastPageCount = splitted3[0];
				pageCount = Integer.parseInt(lastPageCount);
			}

			int pageNumber = 1;
			while (pageNumber <= pageCount) {
				try {
					String queryUrl = pageUrl + "&page=" + pageNumber;
					LOG.info("Executing [" + queryUrl);
					response = makeRestCall(queryUrl, repo.getUserId(), decryptedPassword);
					JSONArray jsonArray = paresAsArray(response);
					for (Object item : jsonArray) {
						JSONObject jsonObject = (JSONObject) item;
						String message = str(jsonObject, "title");
						String number = str(jsonObject, "number");
						String sha = str(jsonObject, "merge_commit_sha");

						JSONObject userObject = (JSONObject) jsonObject.get("user");
						String name = str(userObject, "login");
						String created = str(jsonObject, "created_at");
						String merged = str(jsonObject, "merged_at");
						String closed = str(jsonObject, "closed_at");
						long createdTimestamp = new DateTime(created).getMillis();
						String commentsUrl = str(jsonObject, "comments_url");
						String reviewCommentsUrl = str(jsonObject, "review_comments_url");

						GitRequest pull = new GitRequest();

						if (merged != null && merged.length() >= 10) {
							long mergedTimestamp = new DateTime(merged).getMillis();
							pull.setScmCommitTimestamp(mergedTimestamp);
							pull.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
						}
						pull.setUserId(name);
						pull.setScmUrl(repo.getRepoUrl());
						pull.setScmBranch(branch);
						pull.setTimestamp(createdTimestamp);
						pull.setScmRevisionNumber(sha);
						pull.setScmCommitLog(message);
						pull.setCreatedAt(createdTimestamp);
						pull.setClosedAt(new DateTime(closed).getMillis());
						pull.setMergedAt(new DateTime(merged).getMillis());
						pull.setNumber(number);
						pull.setRequestType("pull");
						pull.setState("open");
						if (merged != null) {
							pull.setState("merged");
						} else if (closed != null) {
							pull.setState("closed");
						}

						String reponameArray[] = pageUrl.split("/");

//						GitRequest preExistinggitRequest = gitRequestRepository.findByOrgNameAndRepoNameAndNumberAndType(
//								reponameArray[ORG_POS_IN_API_URL], reponameArray[REPO_POS_IN_API_URL],
//								number,"pull");
//						if ( preExistinggitRequest!= null) {
//							gitRequestRepository.delete(preExistinggitRequest);
//						}
						pull.setOrgName(reponameArray[ORG_POS_IN_API_URL]);
						pull.setRepoName(reponameArray[REPO_POS_IN_API_URL]);

						JSONObject headObject = (JSONObject) jsonObject.get("head");
						JSONObject headRepoObject = (JSONObject) headObject.get("repo");
						if (headObject != null) {
							pull.setHeadSha(str(headObject, "sha"));
							pull.setSourceBranch(str(headObject, "ref"));
						}
						if (headRepoObject != null) {
							pull.setSourceRepo(str(headRepoObject, "full_name"));
						}

						JSONObject baseObject = (JSONObject) jsonObject.get("base");
						JSONObject baseRepoObject = (JSONObject) baseObject.get("repo");
						if (baseObject != null) {
							pull.setBaseSha(str(baseObject, "sha"));
							pull.setTargetBranch(str(baseObject, "ref"));
						}
						if (baseRepoObject != null) {
							pull.setTargetRepo(str(baseRepoObject, "full_name"));
						}

						pull.setCommentsUrl(commentsUrl);
						List<Comment> comments = getComments(commentsUrl, repo);
						pull.setComments(comments);
						List<Comment> reviewComments = getComments(reviewCommentsUrl, repo);
						pull.setReviewComments(reviewComments);
						pull.setReviewCommentsUrl(reviewCommentsUrl);
						pulls.add(pull);
					}

					if (pageNumber == pageCount)
						break;
					if (jsonArray == null || jsonArray.isEmpty()) {
						pageNumber = pageCount;
						break;
					}
				} catch (RestClientException re) {
					LOG.error(re.getMessage());
					pageNumber = pageCount;
				}
				pageNumber++;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return pulls;
	}

	@Override
	@SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength"}) // agreed, fixme
	public List<GitRequest> getIssues(GitHubRepo repo, boolean firstRun, GitRequestRepository issueRepository) {

		List<GitRequest> issues = new ArrayList<>();

		// format URL
		String repoUrl = (String) repo.getOptions().get("url");
		if (repoUrl.endsWith(".git"))
			repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));

		URL url;
		String hostName = "";
		String protocol = "";
		try {
			url = new URL(repoUrl);
			hostName = url.getHost();
			protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}
		String hostUrl = protocol + "://" + hostName + "/";
		String repoName = repoUrl.substring(hostUrl.length(), repoUrl.length());
		String apiUrl;
		if (hostName.startsWith(PUBLIC_GITHUB_HOST_NAME)) {
			apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + repoName;
		} else {
			apiUrl = protocol + "://" + hostName + SEGMENT_API + repoName;
		}
		LOG.debug("API URL IS:"+apiUrl);


		String pageUrl = apiUrl.concat("/issues?state=all");

		// decrypt password
		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}

		try {
			ResponseEntity<String> response = makeRestCall(pageUrl, repo.getUserId(), decryptedPassword);

			HttpHeaders headers = response.getHeaders();
			List<String> pagevalues = headers.get("Link");
			int pageCount = 0;

			if (pagevalues == null) {
				pageCount = 1;
			} else {
				String[] splited1 = pagevalues.get(0).split(";");
				String[] splited2 = splited1[1].split("=");
				String[] splitted3 = splited2[3].split(">");
				String lastPageCount = splitted3[0];
				pageCount = Integer.parseInt(lastPageCount);
			}

			int pageNumber = 1;
			while (pageNumber <= pageCount) {
				try {
					String queryUrl = pageUrl + "&page=" + pageNumber;
					LOG.info("Executing [" + queryUrl);
					response = makeRestCall(queryUrl, repo.getUserId(), decryptedPassword);
					JSONArray jsonArray = paresAsArray(response);
					for (Object item : jsonArray) {
						JSONObject jsonObject = (JSONObject) item;

						//pull requests are also issues
						if(jsonObject.get("pull_request") != null) {
							continue;
						}

						String message = str(jsonObject, "title");
						String number = str(jsonObject, "number");

						JSONObject userObject = (JSONObject) jsonObject.get("user");
						String name = str(userObject, "login");
						String created = str(jsonObject, "created_at");
						String closed = str(jsonObject, "closed_at");
						long createdTimestamp = new DateTime(created).getMillis();

						GitRequest issue = new GitRequest();

						if (closed != null && closed.length() >= 10) {
							long mergedTimestamp = new DateTime(closed).getMillis();
							issue.setScmCommitTimestamp(mergedTimestamp);
							issue.setResolutiontime((mergedTimestamp - createdTimestamp) / (24 * 3600000));
						}
						issue.setUserId(name);
						issue.setScmUrl(repo.getRepoUrl());
						issue.setTimestamp(createdTimestamp);
						issue.setScmRevisionNumber(number);
						issue.setScmCommitLog(message);
						issue.setCreatedAt(createdTimestamp);
						issue.setClosedAt(new DateTime(closed).getMillis());
						issue.setNumber(number);
						issue.setRequestType("issue");
						if (closed != null) {
							issue.setState("closed");
						}
						else {
							issue.setState("open");
						}
						String reponameArray[] = pageUrl.split("/");

//						GitRequest preExistingIssue = issueRepository.findByOrgNameAndRepoNameAndNumberAndType(
//								reponameArray[ORG_POS_IN_API_URL], reponameArray[REPO_POS_IN_API_URL],
//								number, "issue");
//						if ( preExistingIssue!= null) {
//							issueRepository.delete(preExistingIssue);
//						}
						issue.setOrgName(reponameArray[6]);
						issue.setRepoName(reponameArray[7]);
						issues.add(issue);
					}

					if (pageNumber == pageCount)
						break;
					if (jsonArray == null || jsonArray.isEmpty()) {
						pageNumber = pageCount;
						break;
					}
				} catch (RestClientException re) {
					LOG.error(re.getMessage());
					pageNumber = pageCount;
				}
				pageNumber++;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return issues;
	}

	@SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"}) // agreed, fixme
	public List<Comment> getComments(String commentsUrl, GitHubRepo repo) throws RestClientException {

		List<Comment> comments = new ArrayList<>();

		// decrypt password
		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}
		boolean lastPage = false;
		int pageNumber = 1;
		String queryUrlPage = commentsUrl;
		while (!lastPage) {
			ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword);
			JSONArray jsonArray = paresAsArray(response);
			for (Object item : jsonArray) {
				JSONObject jsonObject = (JSONObject) item;

				Comment comment = new Comment();
				JSONObject userJsonObj = (JSONObject)jsonObject.get("user");
				comment.setUser((String)userJsonObj.get("login"));
				long crt = new DateTime(str(jsonObject, "created_at")).getMillis();
				comment.setCreatedAt(crt);
				long upd = new DateTime(str(jsonObject, "updated_at")).getMillis();
				comment.setUpdatedAt(upd);
				comment.setBody(str(jsonObject, "body"));
				comments.add(comment);
			}
			if (CollectionUtils.isEmpty(jsonArray)) {
				lastPage = true;
			} else {
				lastPage = isThisLastPage(response);
				pageNumber++;
				queryUrlPage = commentsUrl + "&page=" + pageNumber;
			}
		}
		return comments;
	}
}
