package com.capitalone.dashboard.collecteur;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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
    
    @Autowired
    public DefaultGitlabGitClient(GitlabSettings gitlabSettings,
                                       Supplier<RestOperations> restOperationsSupplier) {
        this.gitlabSettings = gitlabSettings;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
    public List<Commit> getCommits(GitlabGitRepo repo) {
        List<Commit> commits = new ArrayList<>();
        String repoUrl = this.buildApiUrl(repo);

		String repoUrlwithToken = repoUrl + "?&private_token=" + gitlabSettings.getApiToken();

		ResponseEntity<String> response = makeRestCall(repoUrlwithToken, null, null);
		JSONArray jsonArray = paresAsArray(response);
		for (Object item : jsonArray) {
			JSONObject jsonObject = (JSONObject) item;
			commits.add(buildCommit(jsonObject, repo.getRepoUrl(), repo.getBranch()));
		}

        return commits;
    }

     /**
	 * Build API Url
	 *
	 * @param repo
	 *            GitlabGitRepo repo.getOptions().get("url") =
	 *            https://gitlab.company.com/team/reponame.git
	 * @return url api format =
	 *         https://gitlab.company.com/api/v3/projects/team%2Freponame/repository/commits/
	 */
	private String buildApiUrl(GitlabGitRepo repo) {
        String repoUrl = (String) repo.getOptions().get("url");
        
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

		return apiUrl;
    }

	private Commit buildCommit(JSONObject jsonObject, String repoUrl, String repoBranch) {
		String author = str(jsonObject, "author_name");
		String message = str(jsonObject, "message");
		String id = str(jsonObject, "id");
		long timestamp = new DateTime(str(jsonObject, "created_at")).getMillis();

		Commit commit = new Commit();
		commit.setTimestamp(System.currentTimeMillis());
		commit.setScmUrl(repoUrl);
		// TODO: figure out why branch name isn't getting through
		commit.setScmBranch(repoBranch);
		commit.setScmRevisionNumber(id);
		commit.setScmAuthor(author);
		commit.setScmCommitLog(message);
		commit.setScmCommitTimestamp(timestamp);
		commit.setNumberOfChanges(159753);
		// TODO: figure out commit type and parents
		return commit;
	}

	private ResponseEntity<String> makeRestCall(String url, String userId, String password) {
		trustSelfSignedSSL();
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			// e.printStackTrace();
		}
		// Basic Auth only.
		if (!"".equals(userId) && !"".equals(password)) {
			return restOperations.exchange(uri, HttpMethod.GET, new HttpEntity<>(createHeaders(userId, password)),
					String.class);

		} else {
			return restOperations.exchange(uri, HttpMethod.GET, null, String.class);
		}

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
}
