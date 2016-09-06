package com.capitalone.dashboard.util;


import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class GitBranchSpec {
    private String name;

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        } else if (name.length() == 0) {
            this.name = "**";
        } else {
            this.name = name.trim();
        }
    }


    public GitBranchSpec(String name) {
        setName(name);
    }

    public String toString() {
        return name;
    }

    public boolean matches(String item) {
        return getPattern().matcher(item).matches();
    }


    private Pattern getPattern() {
        String expandedName = name;
        // use regex syntax directly if name starts with colon
        if ((expandedName.charAt(0) == ':') && (expandedName.length() > 1)) {
            String regexSubstring = expandedName.substring(1, expandedName.length());
            return Pattern.compile(regexSubstring);
        }
        // build a pattern into this builder
        StringBuilder builder = new StringBuilder(100);

        // for legacy reasons (sic) we do support various branch spec format to declare remotes / branches
        builder.append("(refs/heads/");


        // if an unqualified branch was given, consider all remotes (with various possible syntaxes)
        // so it will match branches from  any remote repositories as the user probably intended
        if (!expandedName.contains("**") && !expandedName.contains("/")) {
            builder.append("|refs/remotes/[^/]+/|remotes/[^/]+/|[^/]+/");
        } else {
            builder.append("|refs/remotes/|remotes/");
        }
        builder.append(")?");

        // was the last token a wildcard?
        boolean foundWildcard = false;

        // split the string at the wildcards
        StringTokenizer tokenizer = new StringTokenizer(expandedName, "*", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            // is this token is a wildcard?
            if (token.equals("*")) {
                // yes, was the previous token a wildcard?
                if (foundWildcard) {
                    // yes, we found "**"
                    // match over any number of characters
                    builder.append(".*");
                    foundWildcard = false;
                } else {
                    // no, set foundWildcard to true and go on
                    foundWildcard = true;
                }
            } else {
                // no, was the previous token a wildcard?
                if (foundWildcard) {
                    // yes, we found "*" followed by a non-wildcard
                    // match any number of characters other than a "/"
                    builder.append("[^/]*");
                    foundWildcard = false;
                }
                // quote the non-wildcard token before adding it to the phrase
                builder.append(Pattern.quote(token));
            }
        }

        // if the string ended with a wildcard add it now
        if (foundWildcard) {
            builder.append("[^/]*");
        }

        return Pattern.compile(builder.toString());
    }
}

