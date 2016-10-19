package com.capitalone.dashboard.collecteur;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.util.Supplier;

/**
 * Created by benathmane on 23/06/16.
 */

@Component
@SuppressWarnings({"PMD"})
public class DefaultGitlabGitClient implements  GitlabGitClient {
    private final GitlabSettings gitlabSettings;

    private static final Log LOG = LogFactory.getLog(DefaultGitlabGitClient.class);

    private final RestOperations restOperations;

    private static final String SEGMENT_API = "/api/v3/projects/";
    private static final String PUBLIC_GITLAB_HOST_NAME = "gitlab.company.com";
	private static final int FIRST_RUN_HISTORY_DEFAULT = 14;
    
    @Autowired
    public DefaultGitlabGitClient(GitlabSettings gitlabSettings,
                                       Supplier<RestOperations> restOperationsSupplier) {
        this.gitlabSettings = gitlabSettings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
	public List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();
		String apiUrl = buildApiUrl(repo, firstRun);

		ResponseEntity<String> response = makeRestCall(apiUrl);
		JSONArray jsonArray = paresAsArray(response);
		for (Object item : jsonArray) {
			JSONObject jsonObject = (JSONObject) item;
			commits.add(buildCommit(jsonObject, repo.getRepoUrl(), repo.getBranch()));
		}

        return commits;
    }

	private String buildApiUrl(GitlabGitRepo repo, boolean firstRun) {
		String repoUrl = repo.getRepoUrl();
        
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
        }
        
        String repoName = "";
		try {
	        URL url = new URL(repoUrl);
			repoName = url.getFile();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
		}
      
		repoName = repoName.substring(repoName.indexOf("/") + 1, repoName.length());
		repoName = repoName.replace("/", "%2F");

        String providedGitLabHost = gitlabSettings.getHost();
		String apiHost;
		if (StringUtils.isBlank(providedGitLabHost)) {
			apiHost = PUBLIC_GITLAB_HOST_NAME;
		} else {
        	apiHost = providedGitLabHost;
        }
        
		String apiUrl = "https://" + apiHost + SEGMENT_API + repoName + "/repository/commits/";

		String date = getDateForCommits(repo, firstRun);

		String apiUrlwithToken = apiUrl + "?ref_name=" + repo.getBranch() + "&since=" + date + "&private_token="
				+ gitlabSettings.getApiToken();

		return apiUrlwithToken;
    }

	private String getDateForCommits(GitlabGitRepo repo, boolean firstRun) {
		Date dt;
		if (firstRun) {
			int firstRunDaysHistory = gitlabSettings.getFirstRunHistoryDays();
			if (firstRunDaysHistory > 0) {
				dt = getDate(new Date(), -firstRunDaysHistory, 0);
			} else {
				dt = getDate(new Date(), -FIRST_RUN_HISTORY_DEFAULT, 0);
			}
		} else {
			dt = getDate(new Date(repo.getLastUpdated()), 0, -10);
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String thisMoment = df.format(dt);
		return thisMoment;
	}

	private Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateInstance);
		cal.add(Calendar.DATE, offsetDays);
		cal.add(Calendar.MINUTE, offsetMinutes);
		return cal.getTime();
	}

	private Commit buildCommit(JSONObject jsonObject, String repoUrl, String repoBranch) {
		String author = str(jsonObject, "author_name");
		String message = str(jsonObject, "message");
		String id = str(jsonObject, "id");
		long timestamp = new DateTime(str(jsonObject, "created_at")).getMillis();

		Commit commit = new Commit();
		commit.setTimestamp(System.currentTimeMillis());
		commit.setScmUrl(repoUrl);
		commit.setScmBranch(repoBranch);
		commit.setScmRevisionNumber(id);
		commit.setScmAuthor(author);
		commit.setScmCommitLog(message);
		commit.setScmCommitTimestamp(timestamp);
		commit.setNumberOfChanges(1);
		// TODO: figure out commit type and parents
		return commit;
	}

	private ResponseEntity<String> makeRestCall(String url) {
		trustSelfSignedSSL();
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}

		return restOperations.exchange(uri, HttpMethod.GET, null, String.class);

	}

	private void trustSelfSignedSSL() {
		try {
			final SSLContext ctx = SSLContext.getInstance("TLS");
			final X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(final X509Certificate[] xcs, final String string)
						throws CertificateException {
					// do nothing
				}

				public void checkServerTrusted(final X509Certificate[] xcs, final String string)
						throws CertificateException {
					// do nothing
				}

				public X509Certificate[] getAcceptedIssuers() {
					X509Certificate[] n = new X509Certificate[0];
					return n;

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLContext.setDefault(ctx);
		} catch (final Exception ex) {
			// ex.printStackTrace();
		}
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
