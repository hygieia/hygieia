package com.capitalone.dashboard.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the GitHub collector.
 */
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubSettings {
    private String proxyPort;
    private String proxy;
    private String cron;
    private String host;
    private String key;
        @Value("${github.firstRunHistoryDays:14}")
    private int firstRunHistoryDays;
    private List<String> notBuiltCommits;
        @Value("${github.errorThreshold:2}")
    private int errorThreshold;
        @Value("${github.rateLimitThreshold:10}")
        private int rateLimitThreshold;
        private String personalAccessToken;

         public String getProxyPort() {          //getters and setters 
                return proxyPort;
         } 

         public void setProxyPort(String proxyPort){
                this.proxyPort=proxyPort;
         }

         public String getProxy() {
                return proxy;
        }
        public void setProxy(String proxy) {
                this.proxy = proxy;
        }
        public String getHost() {
                return host;
        }

        public void setHost(String host) {
                this.host = host;
        }

        public String getCron() {
        return cron;
        }

    public void setCron(String cron) {
        this.cron = cron;
        }

        public String getKey() {
                return key;
        }

        public void setKey(String key) {
                this.key = key;
        }

    public int getFirstRunHistoryDays() {
                return firstRunHistoryDays;
        }

        public void setFirstRunHistoryDays(int firstRunHistoryDays) {
                this.firstRunHistoryDays = firstRunHistoryDays;
        }

    public List<String> getNotBuiltCommits() {
        return notBuiltCommits;
    }

    public void setNotBuiltCommits(List<String> notBuiltCommits) {
        this.notBuiltCommits = notBuiltCommits;
    }

        public int getErrorThreshold() {
                return errorThreshold;
        }

        public void setErrorThreshold(int errorThreshold) {
                this.errorThreshold = errorThreshold;
        }

        public int getRateLimitThreshold() {
                return rateLimitThreshold;
        }

        public void setRateLimitThreshold(int rateLimitThreshold) {
                this.rateLimitThreshold = rateLimitThreshold;
        }

        public String getPersonalAccessToken() {
                return personalAccessToken;
        }

        public void setPersonalAccessToken(String personalAccessToken) {
                this.personalAccessToken = personalAccessToken;
        }
}
