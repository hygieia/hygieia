package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Developer;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.repository.IssueRepository;
import com.capitalone.dashboard.repository.PullRepository;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.Supplier;
import com.capitalone.dashboard.utils.DeveloperDataClientImpl;
import org.apache.commons.codec.binary.Base64;
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
import java.util.*;

import static com.capitalone.dashboard.model.QIssue.issue;
import static com.capitalone.dashboard.model.QPull.pull;
import static org.eclipse.jdt.internal.compiler.parser.Parser.name;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultGitHubClient implements GitHubClient {
	private static final Log LOG = LogFactory.getLog(DefaultGitHubClient.class);

	public static Map  <String, Developer> devs = new HashMap();
	private final GitHubSettings settings;
	private final DeveloperDataSettings devDataSettings;
	private final DeveloperDataClient devDataClient = new DeveloperDataClientImpl();

	private final RestOperations restOperations;


	private static final String REPO_SEGMENT_API = "/api/v3/repos/";
	private static final String PUBLIC_GITHUB_REPO_HOST = "api.github.com/repos/";
	private static final String ORG_SEGMENT_API = "/api/v3/orgs/";
	private static final String PUBLIC_GITHUB_ORG_HOST = "api.github.com/orgs/";

	private static final String PUBLIC_GITHUB_HOST_NAME = "github.com";
	private static final int FIRST_RUN_HISTORY_DEFAULT = 150;

	/*@Autowired
	public DefaultGitHubClient(GitHubSettings settings,
							   Supplier<RestOperations> restOperationsSupplier) {
		this.settings = settings;
		this.restOperations = restOperationsSupplier.get();
		//this.devDataSettings = null;
	}
	*/



	@Autowired

	public DefaultGitHubClient(GitHubSettings settings, DeveloperDataSettings developerDataSettings,
							   Supplier<RestOperations> restOperationsSupplier) {
		this.settings = settings;
		this.restOperations = restOperationsSupplier.get();
		this.devDataSettings = developerDataSettings;
	}


	@Override
	@SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength"}) // agreed, fixme
	public List<Commit> getCommits(GitHubRepo repo, boolean firstRun) {
		List<Commit> commits = new ArrayList<>();

		String apiUrl = getUrl(repo, REPO_SEGMENT_API);

		Date dt;
		if (firstRun) {
			int firstRunDaysHistory = settings.getFirstRunHistoryDays();
			if (firstRunDaysHistory > 0) {
				dt = getDate(new Date(), -firstRunDaysHistory, 0);
			} else {
				dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
			}
		} else {
			dt = getDate(new Date(repo.getLastUpdated()), -150, -10);
		}
		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(dt);
		String thisMoment = String.format("%tFT%<tRZ", cal);
		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}
		//Find All Repo's of this org
		String repoUrl = getOrgUrl(repo).concat("repos");
		List <String> repos = new ArrayList<>();

		try {
			ResponseEntity<String> response = makeRestCall(repoUrl, repo.getUserId(), decryptedPassword);
			JSONArray jsonArray = paresAsArray(response);
			for (Object item : jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				String name = str(jsonObject, "name");
				repos.add(name);
			}
		}
		catch (RestClientException re) {
			LOG.error(re.getMessage());
		}

		Iterator iter = repos.iterator();
		while (iter.hasNext()) {
			String repoName = (String )iter.next();
			String queryUrl = apiUrl.concat(repoName + "/commits?sha=" + repo.getBranch()
					+ "&since=" + thisMoment);

			boolean lastPage = false;
			int pageNumber = 1;
			String queryUrlPage = queryUrl;
			while (!lastPage) {
				try {
					ResponseEntity<String> response = makeRestCall(queryUrlPage, repo.getUserId(), decryptedPassword);
					JSONArray jsonArray = paresAsArray(response);
					for (Object item : jsonArray) {
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
						commit.setRepoName(repoName);
						commits.add(commit);
					}
					if (jsonArray == null || jsonArray.isEmpty()) {
						lastPage = true;
					} else {
						lastPage = isThisLastPage(response);
						pageNumber++;
						queryUrlPage = queryUrl + "&page=" + pageNumber;
					}

				} catch (RestClientException re) {
					LOG.error(re.getMessage() + ":" + queryUrl);
					lastPage = true;

				}
			}
		}
		return commits;
	}
	@Override
	@SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength"}) // agreed, fixme
	public List<Pull> getPulls(GitHubRepo repo, boolean firstRun, PullRepository pullRepository) {
		List<Pull> pulls = new ArrayList<>();

		String apiUrl = getUrl(repo, REPO_SEGMENT_API);

		Date dt;
		if (firstRun) {
			int firstRunDaysHistory = settings.getFirstRunHistoryDays();
			if (firstRunDaysHistory > 0) {
				dt = getDate(new Date(), -firstRunDaysHistory, 0);
			} else {
				dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
			}
		} else {
			dt = getDate(new Date(repo.getLastUpdated()), -150, -10);
		}
		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(dt);
		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}
		String repoUrl = getOrgUrl(repo).concat("repos");
		List <String> repos = new ArrayList<>();

		try {
			ResponseEntity<String> response = makeRestCall(repoUrl, repo.getUserId(), decryptedPassword);
			JSONArray jsonArray = paresAsArray(response);
			for (Object item : jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				String name = str(jsonObject, "name");
				repos.add(name);
			}
		}
		catch (RestClientException re) {
			LOG.error(re.getMessage());
		}

		Iterator iter = repos.iterator();
		while (iter.hasNext()) {
			String repoName = (String )iter.next();
			String pageUrl = apiUrl.concat(repoName + "/pulls?state=all");
			ResponseEntity<String> response = makeRestCall(pageUrl, repo.getUserId(), decryptedPassword);

			HttpHeaders headers = response.getHeaders();
			List<String> pagevalues = headers.get("Link");
			int pageCount=0;

			if (pagevalues == null) {
				pageCount = 1;
			}
			else {
				LOG.error("list is " + pagevalues.get(0));
				String[] splited1 = pagevalues.get(0).split(";");
				String[] splited2 = splited1[1].split("=");
				String[] splitted3 = splited2[3].split(">");
				String lastPageCount = splitted3[0];
				LOG.error(lastPageCount);
				pageCount = Integer.parseInt(lastPageCount);
			}

			int pageNumber = 1;
			while (pageNumber <= pageCount ) {
				try {
					String queryUrl = pageUrl + "&page=" + pageNumber;
					response = makeRestCall(queryUrl, repo.getUserId(), decryptedPassword);
					JSONArray jsonArray = paresAsArray(response);
					for (Object item : jsonArray) {
						JSONObject jsonObject = (JSONObject) item;
						String message = str(jsonObject, "title");
						String number = str(jsonObject, "number");

						if (pullRepository.findByOrgNameAndRepoNameAndNumber(repo.getOrgName(),repoName, number) != null)
						{
							LOG.error("Pull " + number + " already exists for " + repoName + ".. move to next repo");
							pageNumber = pageCount;
							break;
						}
						JSONObject userObject = (JSONObject) jsonObject.get("user");
						String name = str(userObject, "login");
						String created=str(jsonObject, "created_at");
						String merged=str(jsonObject, "merged_at");
						String closed=str(jsonObject, "closed_at");
						long createdTimestamp = new DateTime(created).getMillis();

						Pull pull = new Pull();

						if (merged != null && merged.length() >= 10) {
							long mergedTimestamp = new DateTime(merged).getMillis();
							pull.setScmCommitTimestamp(mergedTimestamp);
							pull.setResolutiontime(mergedTimestamp - createdTimestamp);
						}
						pull.setScmUrl(repo.getRepoUrl());
						pull.setTimestamp(createdTimestamp);
						pull.setScmRevisionNumber(number);
						pull.setScmCommitLog(message);
						pull.setCreatedAt(created);
						pull.setClosedAt(closed);
						pull.setMergedAt(merged);
						pull.setOrgName(repo.getOrgName());
						pull.setNumber(number);
						pull.setRepoName(repoName);
						pull.setNumberOfChanges(1);
						buildPullDeveloper(pull, name);
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
		}
		return pulls;
	}

	@Override
	@SuppressWarnings({"PMD.NPathComplexity","PMD.ExcessiveMethodLength"}) // agreed, fixme
	public List<Issue> getIssues(GitHubRepo repo, boolean firstRun, IssueRepository issueRepository) {

		List<Issue> issues = new ArrayList<>();

		String apiUrl = getUrl(repo, REPO_SEGMENT_API);
		Date dt;
		if (firstRun) {
			int firstRunDaysHistory = settings.getFirstRunHistoryDays();
			if (firstRunDaysHistory > 0) {
				dt = getDate(new Date(), -firstRunDaysHistory, 0);
			} else {
				dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
			}
		} else {
			dt = getDate(new Date(repo.getLastUpdated()), -150, -10);
		}
		Calendar calendar = new GregorianCalendar();
		TimeZone timeZone = calendar.getTimeZone();
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(dt);
		//String thisMoment = String.format("%tFT%<tRZ", cal);

		String decryptedPassword = "";
		if (repo.getPassword() != null && !repo.getPassword().isEmpty()) {
			try {
				decryptedPassword = Encryption.decryptString(
						repo.getPassword(), settings.getKey());
			} catch (EncryptionException e) {
				LOG.error(e.getMessage());
			}
		}
		//Find All Repo's of this org
		String repoUrl = getOrgUrl(repo).concat("repos");
		List <String> repos = new ArrayList<>();

		try {
				ResponseEntity<String> response = makeRestCall(repoUrl, repo.getUserId(), decryptedPassword);
				JSONArray jsonArray = paresAsArray(response);
				for (Object item : jsonArray) {
					JSONObject jsonObject = (JSONObject) item;
					String name = str(jsonObject, "name");
					repos.add(name);
				}
		}
		catch (RestClientException re) {
			LOG.error(re.getMessage());
		}


		Iterator iter = repos.iterator();
		while (iter.hasNext()) {

			String repoName = (String )iter.next();
			String pageUrl = apiUrl.concat(repoName + "/issues?state=all");
			ResponseEntity<String> response = makeRestCall(pageUrl, repo.getUserId(), decryptedPassword);

			HttpHeaders headers = response.getHeaders();
			List<String> pagevalues = headers.get("Link");
			int pageCount=0;

			if (pagevalues == null) {
				pageCount = 1;
			}
			else {
				LOG.error("list is " + pagevalues.get(0));
				String[] splited1 = pagevalues.get(0).split(";");
				String[] splited2 = splited1[1].split("=");
				String[] splitted3 = splited2[3].split(">");
				String lastPageCount = splitted3[0];
				LOG.error(lastPageCount);
				pageCount = Integer.parseInt(lastPageCount);
			}

			int pageNumber = 1;
			while (pageNumber <= pageCount ) {
				try {
					String queryUrl = pageUrl + "&page=" + pageNumber;
					response = makeRestCall(queryUrl, repo.getUserId(), decryptedPassword);

					JSONArray jsonArray = paresAsArray(response);
					for (Object item : jsonArray) {
						JSONObject jsonObject = (JSONObject) item;
						String message = str(jsonObject, "title");
						String number = str(jsonObject, "number");
						if (issueRepository.findByOrgNameAndRepoNameAndNumber(repo.getOrgName(),repoName, number) != null)
						{
							LOG.error("Issue " + number + " already exists for " + repoName + ".. move to next repo");
							pageNumber = pageCount;
							break;
						}
						JSONObject userObject = (JSONObject) jsonObject.get("user");
						String name = str(userObject, "login");
						String created = str(jsonObject, "created_at");
						String closed = str(jsonObject, "closed_at");
						long createdTimestamp = new DateTime(created).getMillis();
						Issue issue = new Issue();
						if (closed != null && closed.length() >= 10) {
							long closedTimestamp = new DateTime(closed).getMillis();
							issue.setScmCommitTimestamp(closedTimestamp);
							issue.setResolutiontime(closedTimestamp - createdTimestamp);
						}
						issue.setScmUrl(repo.getRepoUrl());
						issue.setScmRevisionNumber(number);
						issue.setScmCommitLog(message);
						issue.setTimestamp(createdTimestamp);
						issue.setCreatedAt(created);
						issue.setClosedAt(closed);
						issue.setOrgName(repo.getOrgName());
						issue.setNumber(number);
						issue.setRepoName(repoName);
						issue.setNumberOfChanges(1);
						buildIssueDeveloper(issue, name);
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
		}
		return issues;
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


	private String getUrl(GitHubRepo repo, String urlPart)
	{
		// format URL
		String repoUrl = (String) repo.getOptions().get("url");
		if (repoUrl.endsWith(".git")) {
			repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
		}
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
			apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + repoName ;
		} else {
			apiUrl = protocol + "://" + hostName + urlPart + repoName ;
			LOG.debug("API URL IS:"+apiUrl);
		}
		return apiUrl;

	}
	private String getOrgUrl(GitHubRepo repo)
	{
		// format URL
		String repoUrl = (String) repo.getOptions().get("url");
		if (repoUrl.endsWith(".git")) {
			repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
		}
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
			apiUrl = protocol + "://" + PUBLIC_GITHUB_ORG_HOST + repoName ;
		} else {
			apiUrl = protocol + "://" + hostName + ORG_SEGMENT_API + repoName ;
			LOG.debug("API URL IS:"+apiUrl);
		}
		return apiUrl;
	}
	private void buildPullDeveloper (Pull scm, String name) {

		Developer dev = (Developer) devs.get(name);
		if (dev == null) {
			try {
				dev = devDataClient.getDeveloper(name, devDataSettings);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		if (dev != null) {
			scm.setDepartmentId(dev.getDepartmentId());
			scm.setLevelOneMgr(dev.getLevelOneMgr());
			scm.setLevelTwoMgr(dev.getLevelTwoMgr());
			scm.setJobLevel(dev.getJobLevel());
			scm.setManager(dev.getManager());
			scm.setUserId(name);
			scm.setDeveloperName(dev.getName());
			scm.setDepartmentName(dev.getDepartmentname());
		} else {
			scm.setDeveloperName(name);
		}
		devs.put(name, dev);
	}
	private void buildIssueDeveloper (Issue scm, String name) {

		Developer dev = (Developer) devs.get(name);
		if (dev == null) {
			try {
				dev = devDataClient.getDeveloper(name, devDataSettings);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		if (dev != null) {
			scm.setDepartmentId(dev.getDepartmentId());
			scm.setLevelOneMgr(dev.getLevelOneMgr());
			scm.setLevelTwoMgr(dev.getLevelTwoMgr());
			scm.setJobLevel(dev.getJobLevel());
			scm.setManager(dev.getManager());
			scm.setUserId(name);
			scm.setDeveloperName(dev.getName());
			scm.setDepartmentName(dev.getDepartmentname());
		}else {
			scm.setDeveloperName(name);
		}
		devs.put(name, dev);
	}

}