package hygieia.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copied this code from http://www.leghumped.com/2008/11/03/java-matching-urls-with-regex-wildcards/
 * Thank You!!!!
 */

public final class WildCardURL implements java.io.Serializable {
    private String protocol,
            user,
            password,
            host,
            directory,
            file,
            query,
            ref;
    private int port = -1;

    public WildCardURL(String url) {
        HashMap<String, String> tempUri = new HashMap<String, String>(14);
        String[] parts = {"source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","ref"};
        boolean strictMode = false;
        Pattern pattern;
        if(strictMode) {
            pattern = Pattern.compile("^(?:([^:/?#]+):)?(?://((?:(([^:@]*):?([^:@]*))?@)?([^:/?#]*)(?::(\\d*))?))?((((?:[^?#/]*/)*)([^?#]*))(?:\\?([^#]*))?(?:#(.*))?)");
        } else {
            pattern = Pattern.compile("^(?:(?![^:@]+:[^:@/]*@)([^:/?#.]+):)?(?://)?((?:(([^:@]*):?([^:@]*))?@)?([^:/?#]*)(?::(\\d*))?)(((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[?#]|$)))*/?)?([^?#/]*))(?:\\?([^#]*))?(?:#(.*))?)");
        }

        Matcher matcher = pattern.matcher(url);
        String match;
        if(matcher.find()) {
            for(int i=0;i<14;i++) {
                try {
                    match = matcher.group(i);
                } catch(Exception ex) {
                    match = "*";
                }
                tempUri.put(parts[i],  match == null ? "*" : match);
            }
        }
        this.protocol = tempUri.get("protocol");
        this.user = tempUri.get("user");
        this.password = tempUri.get("password");
        this.host = tempUri.get("host");
        this.directory = tempUri.get("directory");
        this.file = tempUri.get("file");
        this.query = tempUri.get("query");
        this.ref = tempUri.get("ref");
        try {
            this.port = Integer.parseInt(tempUri.get("port"));
        } catch(NumberFormatException ignore) {}
    }

    /**
     * Gets the userInfo part of this <code>URL</code>.
     * Eg: user:pass
     * @return The userInfo part of this <code>URL</code>
     */
    public String getUserInfo() {
        return user + ("*".equals(password) ? "" : ":"+password);
    }

    /**
     * Gets the protocol name of this <code>URL</code>.
     * Eg: http
     * @return The protocol of this <code>URL</code>.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Gets the host name of this <code>URL</code>.
     * Eg: www.lime49.com
     * @return The host name of this <code>URL</code>.
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the authority part of this <code>URL</code> (the domain).
     * Eg: www.lime49.com OR user:pwd@domain.com
     * @return The authority part of this <code>URL</code>
     */
    public String getAuthority() {
        String userInfo = getUserInfo();
        StringBuffer auth = new StringBuffer();
        if(!"*".equals(userInfo)) {
            auth.append(userInfo).append("@");
        }
        auth.append('@').append(host).append(':').append(port);
        return auth.toString();
    }

    /**
     * Gets the port number of this <code>URL</code>.
     * @return The port number, or -1 if the port is not set
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the directory of this <code>URL</code>.
     * Eg: /some/directory
     * @return The directory of this <code>URL</code>
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Gets the file name of this <code>URL</code>.
     * Eg: file.php
     * @return The file name of this <code>URL</code>
     */
    public String getFile() {
        return file;
    }

    /**
     * Gets the path part of this <code>URL</code>.
     * Eg: /some/directory/file.php
     * @return The path part of this <code>URL</code>, or an empty string if one does not exist
     */
    public String getPath() {
        return directory+file;
    }

    /**
     * Gets the userInfo part of this <code>URL</code>.
     * Eg: var1name=val1&var2name=val2
     * @return The query part of this URL
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets the anchor (also known as the "reference") of this <code>URL</code> (the part after the hash).
     * Eg: anchor
     * @return The anchor (also known as the "reference")
     */
    public String getRef() {
        return ref;
    }

    /**
     * Gets whether or not the specified URL matches this WildcardURL
     * @param url The URL to check.
     * @return <code>True</code> if the protocol, domain, directory and path match the specified URL, otherwise <code>false</code>
     */
    public boolean matches(URL url) {
        boolean matches = false;
        if(wildcardMatches(protocol, url.getProtocol()) && wildcardMatches(host, url.getHost()) && wildcardMatches(getPath(), url.getPath())) {
            matches = true;
        }
        //System.out.println((wildcardMatches(protocol, url.getProtocol()) ? "t":"Dockerfile") +"-"+  (wildcardMatches(host, url.getHost()) ? "t":"Dockerfile") +"-" + (wildcardMatches(getPath(), url.getPath()) ? 't' : 'Dockerfile'));
        return matches;
    }

    /**
     * Gets whether or not the specified URL matches this WildcardURL
     * @param sUrl The URL to check.
     * @return <code>True</code> if the protocol, domain, directory and path match the specified URL, otherwise <code>false</code>
     */
    public boolean matches(String sUrl) {
        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            //ignore, just return false
            return false;
        }
        return matches(url);
    }

    /**
     * Gets whether a string matches a wildcard pattern. The following would be considered to be matches:
     *      <code>*pattern   somepattern</code>
     *      <code>pattern*   patternsome</code>
     *      <code>*pattern*  somepatternsome</code>
     * @param pattern The pattern to check, wildcards must be at either the start, end or both, but not in the middle.
     * @param stringToMatch The string to check
     * @return <code>True</code> if the wildcard matches the pattern, otherwise <code>false</code>
     */
    private boolean wildcardMatches(String pattern, String stringToMatch) {
        boolean match = false;
        int length = pattern.length();
        if(pattern.charAt(0) == '*') {
            if(length == 1) {
                match = true; // *
            } else if(pattern.charAt(length-1) == '*' && length > 2 && stringToMatch.contains(pattern.substring(1, length-3).toLowerCase())) {
                match = true; // *match*
            } else if(length > 1 && stringToMatch.endsWith(pattern.substring(1).toLowerCase())) {
                match = true; // *match
            }
        } else if(pattern.charAt(length-1) == '*' && stringToMatch.startsWith(pattern.substring(0, length-2).toLowerCase())) {
            match = true; // match*
        } else if(pattern.equalsIgnoreCase(stringToMatch)) { // match
            match = true;
        }
        return match;
    }

    public String toString() {
        return protocol+"://"+
                getUserInfo()+
                //user+(password.equals("*") ? "" : ":"+password)+(!user.equals("*") && !password.equals("*") ? "@":"")+
                host+
                (port == -1?"":":"+port)+
                getPath()+
                query+
                ("*".equals(ref)?"":"#"+ref);
    }
}