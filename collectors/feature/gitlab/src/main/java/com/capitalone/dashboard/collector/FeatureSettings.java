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
 * Bean to hold settings specific to the Feature collector.
 * 
 */
@Component
@ConfigurationProperties(prefix = "gitlab")
public class FeatureSettings {
    
    private static final Log LOG = LogFactory.getLog(FeatureSettings.class);
	
	private String cron;
	private String protocol;
	private String host;
	private String port;
	private String path;
	private String apiToken;
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
	
	public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

    public boolean isSelfSignedCertificate() {
        return selfSignedCertificate;
    }

    public void setSelfSignedCertificate(boolean selfSignedCertificate) {
        this.selfSignedCertificate = selfSignedCertificate;
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
