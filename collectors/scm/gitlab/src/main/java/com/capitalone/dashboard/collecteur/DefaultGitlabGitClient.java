package com.capitalone.dashboard.collecteur;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

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

    private TrustManager[ ] get_trust_mgr() {
        TrustManager[ ] certs = new TrustManager[ ] {
                new X509TrustManager() {
                    public X509Certificate[ ] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[ ] certs, String t) { }
                    public void checkServerTrusted(X509Certificate[ ] certs, String t) { }
                }
        };
        return certs;
    }

    @Override
    public List<Commit> getCommits(GitlabGitRepo repo) {
        List<Commit> commits = new ArrayList<>();
        String repoUrl = this.normalizeUrl(repo);
        for(int f = 0; f < 20; f++) {
            String repoUrlwithToken = repoUrl + "?page="+f+"&private_token=" + gitlabSettings.getApiToken();
            String https_url = repoUrlwithToken;
            URL url;
            try {

                // Create a context that doesn't check certificates.
                SSLContext ssl_ctx = SSLContext.getInstance("TLS");
                TrustManager[] trust_mgr = get_trust_mgr();
                ssl_ctx.init(null,                // key manager
                        trust_mgr,           // trust manager
                        new SecureRandom()); // random number generator
                HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

                url = new URL(https_url);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                // Guard against "bad hostname" errors during handshake.
                con.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String host, SSLSession sess) {
                        if (host.equals("localhost")) return true;
                        else return false;
                    }
                });

                //dumpl all cert info
                //print_https_cert(con);

                //dump all the content
                //print_content(con);

                if (con != null) {

                    try {

                        //LOG.info("****** Content of the URL ********");
                        BufferedReader br =
                                new BufferedReader(
                                        new InputStreamReader(con.getInputStream()));

                        String input;

                        while ((input = br.readLine()) != null) {
                            JSONParser parser = new JSONParser();
                            try {
                                Object obj = parser.parse(input);
                                JSONArray jsonArray = (JSONArray) obj;
                                int i = 0;
                                for (Object item : jsonArray) {
                                    JSONObject jsonObject = (JSONObject) item;
                                    String author = (String) jsonObject.get("author_name");
                                    String message = (String) jsonObject.get("message");
                                    long timestamp = new DateTime(jsonObject.get("created_at"))
                                            .getMillis();

                                    Commit commit = new Commit();
                                    commit.setTimestamp(System.currentTimeMillis());
                                    commit.setScmUrl(repo.getOptions().get("url").toString());
                                    commit.setScmAuthor(author);
                                    commit.setScmCommitLog(message);
                                    commit.setScmCommitTimestamp(timestamp);
                                    commit.setNumberOfChanges(159753);
                                    i++;

                                /*
                                    LOG.info("getTimestamp " + commit.getTimestamp());
                                    LOG.info("getScmUrl " + commit.getScmUrl());
                                    LOG.info("getScmBranch " + commit.getScmBranch());
                                    LOG.info("getScmCommitLog " + commit.getScmCommitLog());
                                    LOG.info("getScmCommitTimestamp " + commit.getScmCommitTimestamp());
                                    LOG.info("getNumberOfChanges " + commit.getNumberOfChanges());
                                */
                                    commits.add(commit);
                                }
                            } catch (ParseException e) {
                                LOG.error(e);
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                }

            } catch (MalformedURLException e) {
                LOG.error(e);
            } catch (IOException e) {
                LOG.error(e);
            } catch (NoSuchAlgorithmException e) {
                LOG.error(e);
            } catch (KeyManagementException e) {
                LOG.error(e);
            }
        }
        return commits;
    }

    private void print_https_cert(HttpsURLConnection con){
        if(con!=null){

            try {

                LOG.info("Response Code : " + con.getResponseCode());
                LOG.info("Cipher Suite : " + con.getCipherSuite());
                LOG.info("\n");

                Certificate[] certs = con.getServerCertificates();
                for(Certificate cert : certs){
                    LOG.info("Cert Type : " + cert.getType());
                    LOG.info("Cert Hash Code : " + cert.hashCode());
                    LOG.info("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
                    LOG.info("Cert Public Key Format : " + cert.getPublicKey().getFormat());
                    LOG.info("\n");
                }


            } catch (SSLPeerUnverifiedException e) {
                LOG.error(e);
            } catch (IOException e){
                LOG.error(e);
            }
        }
    }

    private void print_content(HttpsURLConnection con){
        if(con!=null){

            try {

                LOG.info("****** Content of the URL ********");
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null){
                    JSONParser parser = new JSONParser();
                    try {
                        Object obj = parser.parse(input);
                        JSONArray jsonArray = (JSONArray) obj;
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObjectRow = (JSONObject) jsonArray.get(i);
                            LOG.info(jsonObjectRow.toString());
                        }
                    } catch (ParseException e) {
                        LOG.error(e);
                    }

                }

                br.close();

            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

     /**
     * Normalize url
     *
     * @param repo GitlabGitRepo
     * repo.getOptions().get("url")  = https://gitlab.company.com/team/reponame.git
     * @return url api format = https://gitlab.company.com/api/v3/projects/team%2Freponame/repository/commits/
     */
    public String normalizeUrl(GitlabGitRepo repo){
        String repoUrl = (String) repo.getOptions().get("url");
        String hostName = "";

        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.lastIndexOf(".git"));
        }
        try {
            URL url = new URL(repoUrl);
            hostName = url.getHost();

            String hostUrl = "https://" + hostName + "/";
            String repoName = repoUrl.substring(hostUrl.length(), repoUrl.length());
            repoName = repoName.replace("/", "%2F");

            repoUrl = "https://" + PUBLIC_GITLAB_HOST_NAME + SEGMENT_API + repoName + "/repository/commits/";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return repoUrl;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }
}
