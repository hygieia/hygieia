package com.capitalone.dashboard.util;

import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 *
 */
public class ProxySettings {
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final boolean isProxySet;


    public static ProxySettings build(String protocol) {
        return new ProxySettings(protocol.toLowerCase(Locale.US));
    }


    public static ProxySettings buildHTTP() {
        return build("http");
    }

    public static ProxySettings buildHTTPS() {
        return build("https");
    }


    public String host() { return this.host; }
    public String user() { return this.user; }
    public String port() { return this.port; }
    public String password() { return this.password; }
    public boolean isProxySet() { return this.isProxySet; }


    ProxySettings(String protocol) {
        String h = "", prt = "", u = "", pw = "";

        // check for properties 1st
        h = System.getProperty(protocol + ".proxyHost", "");
        prt = System.getProperty(protocol + ".proxyPort", "");
        u = System.getProperty(protocol + ".proxyUser", "");
        pw = System.getProperty(protocol + ".proxyPassword", "");

        boolean proxyFound = !Strings.isNullOrEmpty(h);

        if (proxyFound && Strings.isNullOrEmpty(prt)) {
          prt = protocol.equalsIgnoreCase("http") ? "80" : "443";
        } else if (!proxyFound) {
            // 2nd check system env variables
            String proxy = getEnvProxy(protocol);

            if (!Strings.isNullOrEmpty(proxy)) {
                try {
                    URL proxyUrl = new URL(proxy);
                    h = proxyUrl.getHost();
                    prt = Integer.toString(
                            proxyUrl.getPort() == -1 ? proxyUrl.getDefaultPort() : proxyUrl.getPort());

                    String userInfo = proxyUrl.getUserInfo();
                    if (userInfo != null) {
                        String[] info = userInfo.split(":");
                        if (info.length > 0) {
                            u = info[0];
                        }
                        if (info.length > 1) {
                            pw = info[1];
                        }
                    }
                    proxyFound = !Strings.isNullOrEmpty(h);
                } catch (MalformedURLException e) {
                    proxyFound = false;
                }
            }

            if (proxyFound) {
                System.setProperty(protocol + ".proxyHost", h);
                System.setProperty(protocol + ".proxyPort", prt);
                System.setProperty(protocol + ".proxyUser", u);
                System.setProperty(protocol + ".proxyPassword", pw);
            }
        }

        host = h;
        port = prt;
        user = u;
        password = pw;
        isProxySet = proxyFound;
    }

    String getEnvProxy(String protocol) {
        String proxy = getEnv(protocol.toUpperCase(Locale.US) + "_PROXY");
        if (Strings.isNullOrEmpty(proxy)) {
            proxy = getEnv(protocol + "_proxy");
        }
        return proxy;
    }

    String getEnv(String key) {
        return System.getenv(key);
    }

}
