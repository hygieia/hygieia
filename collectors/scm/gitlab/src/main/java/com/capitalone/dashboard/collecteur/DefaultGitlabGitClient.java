package com.capitalone.dashboard.collecteur;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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

    //Gitlab max results per page. Reduces amount of network calls.
    private static final int RESULTS_PER_PAGE = 100;
    
    private final RestOperations restOperations;
    private final GitlabUrlUtility gitlabUrlUtility;
    private final GitlabSettings gitlabSettings;
    private final GitlabResponseMapper responseMapper;
    
    @Autowired
    public DefaultGitlabGitClient(GitlabUrlUtility gitlabUrlUtility, 
    								   GitlabSettings gitlabSettings,
                                       Supplier<RestOperations> restOperationsSupplier, 
                                       GitlabResponseMapper responseMapper) {
        this.gitlabUrlUtility = gitlabUrlUtility;
        this.gitlabSettings = gitlabSettings;
        this.restOperations = restOperationsSupplier.get();
        this.responseMapper = responseMapper;
    }

    @Override
	public List<Commit> getCommits(GitlabGitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();

		URI apiUrl = gitlabUrlUtility.buildApiUrl(repo, firstRun, RESULTS_PER_PAGE);
		String providedApiToken = repo.getUserId();
		String apiToken = (StringUtils.isNotBlank(providedApiToken)) ? providedApiToken:gitlabSettings.getApiToken();

		try{
			boolean lastPage = false;
			int nextPage = 1;
			while(!lastPage) {
				ResponseEntity<String> response = makeRestCall(apiUrl, apiToken);
				List<Commit> pageOfCommits = responseMapper.mapResponse(response.getBody(), repo.getRepoUrl(), repo.getBranch());
				commits.addAll(pageOfCommits);
				if(isLastPage(pageOfCommits.size())) 
					lastPage = true;
				else {
					apiUrl = gitlabUrlUtility.updatePage(apiUrl, nextPage);
					nextPage++;
				}
			}
		} catch (HttpClientErrorException e) {
			LOG.info("Failed to retrieve data from: " + apiUrl);
		}

        return commits;
    }

	private boolean isLastPage(int resultSize) {
		//Gitlab API is supposed to return context of page you are on, but it currently broken for commits.
		if(resultSize < RESULTS_PER_PAGE) 
			return true;
		return false;
	}

	private ResponseEntity<String> makeRestCall(URI url, String apiToken) {
		trustSelfSignedSSL();
		HttpHeaders headers = new HttpHeaders();
		headers.add("PRIVATE-TOKEN", apiToken);
		return restOperations.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
	}

	private void trustSelfSignedSSL() {
		try {
			final SSLContext ctx = SSLContext.getInstance("TLS");
			final X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(final X509Certificate[] xcs, final String string)
						throws CertificateException {
				}

				public void checkServerTrusted(final X509Certificate[] xcs, final String string)
						throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					X509Certificate[] n = new X509Certificate[0];
					return n;

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLContext.setDefault(ctx);
		} catch (final Exception ex) {
			LOG.error(ex.getMessage());
		}
	}
}
