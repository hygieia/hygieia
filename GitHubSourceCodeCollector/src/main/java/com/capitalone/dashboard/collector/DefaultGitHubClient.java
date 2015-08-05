package com.capitalone.dashboard.collector;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.util.Supplier;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGitHubClient implements GitHubClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

	private final GitHubSettings settings;

	private final RestOperations restOperations;
	private final String SEGMENT_API = "/api/v3/repos/";
	private final String PUBLIC_GITHUB_REPO_HOST = "api.github.com/repos/";
	private final String PUBLIC_GITHUB_HOST_NAME =  "github.com";

	@Autowired
	public DefaultGitHubClient(GitHubSettings settings,
			Supplier<RestOperations> restOperationsSupplier) {
		this.settings = settings;
		this.restOperations = restOperationsSupplier.get();
	}

	@Override
	public List<Commit> getCommits(GitHubRepo repo) {

		List<Commit> commits = new ArrayList<>();

		String repoUrl = (String) repo.getOptions().get("url");

		URL url = null;
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
		String apiUrl = "";
		if (hostName.startsWith(PUBLIC_GITHUB_HOST_NAME)) {
			apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + repoName;
		} else {
			 apiUrl = protocol + "://" + hostName + SEGMENT_API
					+ repoName;
		}

		DateTime dt = repo.getLastUpdateTime().minusMinutes(10000); // randomly
																	// chosen 10
																	// minutes.
																	// Need to
																	// refactor
		DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinuteSecond();
		String strDt = fmt.print(dt);
		String queryUrl = apiUrl.concat("/commits?branch=" + repo.getBranch()
				+ "&since=" + strDt);
		for (Object item : paresAsArray(makeRestCall(queryUrl))) {
			JSONObject jsonObject = (JSONObject) item;
			String sha = str(jsonObject, "sha");
			JSONObject commitObject = (JSONObject) jsonObject.get("commit");
			JSONObject authorObject = (JSONObject) commitObject.get("author");
			String message = str(commitObject, "message");
			String author = str(authorObject, "name");
			long timestamp = new DateTime(str(authorObject, "date"))
					.getMillis();
			Commit commit = new Commit();
			commit.setTimestamp(System.currentTimeMillis());
			commit.setScmUrl(repo.getRepoUrl());
			commit.setScmRevisionNumber(sha);
			commit.setScmAuthor(author);
			commit.setScmCommitLog(message);
			commit.setScmCommitTimestamp(timestamp);
			commit.setNumberOfChanges(1);
			commits.add(commit);
		}
		return commits;
	}

	private ResponseEntity<String> makeRestCall(String url) {
		// Not using github auth now. Assuming all public repos.
		return restOperations.exchange(url, HttpMethod.GET, null, String.class);
	}

	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
				String auth = settings.getUsername() + ":"
						+ settings.getPassword();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
						.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
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

}