package com.capitalone.dashboard.collecteur;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
public class DefaultGitlabGitClient implements  GitlabGitClient {

    private static final Log LOG = LogFactory.getLog(DefaultGitlabGitClient.class);

    private static final int RESULTS_PER_PAGE = 100;
    
    private final RestOperations restOperations;
    private final GitlabUrlUtility gitlabUrlUtility;
    
    @Autowired
    public DefaultGitlabGitClient(GitlabUrlUtility gitlabUrlUtility,
                                       Supplier<RestOperations> restOperationsSupplier) {
        this.gitlabUrlUtility = gitlabUrlUtility;
        this.restOperations = restOperationsSupplier.get();
    }

    @Override
	public List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();
		URI apiUrl = gitlabUrlUtility.buildApiUrl(repo, firstRun, RESULTS_PER_PAGE);

		boolean lastPage = false;
		int nextPage = 1;
		while(!lastPage) {
			ResponseEntity<String> response = makeRestCall(apiUrl);
			JSONArray jsonArray = paresAsArray(response);
			for (Object item : jsonArray) {
				JSONObject jsonObject = (JSONObject) item;
				commits.add(buildCommit(jsonObject, repo.getRepoUrl(), repo.getBranch()));
			}
			if(isLastPage(jsonArray.size())) 
				lastPage = true;
			else {
				apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
				nextPage++;
			}
		}

        return commits;
    }

	private boolean isLastPage(int resultSize) {
		if(resultSize < RESULTS_PER_PAGE) 
			return true;
		return false;
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

	private ResponseEntity<String> makeRestCall(URI url) {
		trustSelfSignedSSL();
		return restOperations.exchange(url, HttpMethod.GET, null, String.class);

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
