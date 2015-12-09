package com.capitalone.dashboard.util;

import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;


public class URLParser {
    private String urlString;
    private String userName = "";
    private String token = "";
    private URL url;


    public URLParser(String urlString) throws MalformedURLException {
        this.urlString = urlString;
        parseUrlString();
    }

    private void parseUrlString() throws MalformedURLException {
        String[] userNamePassword = urlString.substring(0, urlString.indexOf("@")).split(":");
        String spec = urlString.substring(urlString.indexOf("@")+1);
        url = new URL(spec);
        if (!StringUtils.isEmpty(userNamePassword)) {
            switch (userNamePassword.length) {
                case 0:
                    break;
                case 1:
                    userName = userNamePassword[0].trim();
                    break;
                case 2:
                    userName = userNamePassword[0].trim();
                    token = userNamePassword[1].trim();
                    break;
                default:
                    throw new MalformedURLException("Incorrect format for URL");
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public URL getUrl () {
        return url;
    }
}


