package com.capitalone.dashboard.collector;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by benathmane on 23/06/16.
 */

/**
 * Bean to hold settings specific to the Gitlab collector.
 */

@Component
@ConfigurationProperties(prefix = "gitlab")
public class GitlabSettings {
    
    private static final Log LOG = LogFactory.getLog(GitlabSettings.class);
    
    private String cron;
    private String protocol;
    private String host;
    private String port;
    private String path;
    private String apiToken;
	private int firstRunHistoryDays;
	private boolean selfSignedCertificate;
	private int apiVersion;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFirstRunHistoryDays() {
		return firstRunHistoryDays;
	}

	public void setFirstRunHistoryDays(int firstRunHistoryDays) {
		this.firstRunHistoryDays = firstRunHistoryDays;
	}

	public boolean isSelfSignedCertificate() {
		return selfSignedCertificate;
	}
	
	public void setSelfSignedCertificate(boolean selfSigned) {
		this.selfSignedCertificate = selfSigned;
	}

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    @PostConstruct
    public void trustSelfSignedCertificatesIfNecessary() {
        if (isSelfSignedCertificate()) {
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

}
