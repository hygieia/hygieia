package com.capitalone.dashboard.bitbucketapi;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;

public class BitbucketUrlParser {

    private static final String SCM_SUFFIXED_WITH_FORWARD_SLASH = "scm/";
    private static final String SCM_PREFIXED_WITH_FORWARD_SLASH = "/scm";
    private static final String GIT_EXTENSION = ".git";
    private static final String HTTPS = "https";
    private static final String SSH = "ssh";

    /**
     * This method constructs Bitbucket url parts from bitbucket url. Usefulness of this method is observed
     * at the time of creating Bitbucket API endpoints.
     *
     * @param rawUrl
     * @return BitbucketUrlParts parts of Bitbucket URL
     */
    public static BitbucketUrlParts parseBitbucketUrl(String rawUrl) {
        String repoUrlProcessed = rawUrl;

        if (repoUrlProcessed.endsWith(GIT_EXTENSION)) {
            repoUrlProcessed =
                    StringUtils.substring(
                            repoUrlProcessed, 0, StringUtils.lastIndexOf(repoUrlProcessed, GIT_EXTENSION));
        }

        URI uri = URI.create(repoUrlProcessed.replaceAll(" ", "%20"));

        String host = uri.getHost();
        int port = uri.getPort();

        String scheme = SSH.equalsIgnoreCase(uri.getScheme()) ? HTTPS : uri.getScheme();

        String path = parseURLPath(uri);

        String[] splitPath = path.split("/");
        String projectKey;
        String repositorySlug;

        if (splitPath.length > 1) {
            projectKey = splitPath[0];
            repositorySlug = StringUtils.substring(path, StringUtils.indexOf(path, '/') + 1);
        } else {
            // Shouldn't get to this case
            projectKey = "";
            repositorySlug = path;
        }

        return new BitbucketUrlParts(scheme, host, port, projectKey, repositorySlug);
    }

    private static String parseURLPath(URI uri) {
        String path = uri.getPath();
        if ((path.startsWith(SCM_SUFFIXED_WITH_FORWARD_SLASH)
                || path.startsWith(SCM_PREFIXED_WITH_FORWARD_SLASH))
                && path.length() > 4) {
            path = path.substring(4);
        }
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * Represents a Bitbucket URL constituents.
     */
    public static class BitbucketUrlParts {
        private final String scheme;
        private final String host;
        private final Integer port;
        private final String projectKey;
        private final String repoKey;

        public BitbucketUrlParts(
                String scheme, String host, int port, String projectKey, String repositorySlug) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.projectKey = projectKey;
            this.repoKey = repositorySlug;
        }

        public String getScheme() {
            return scheme;
        }

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        public String getProjectKey() {
            return projectKey;
        }

        public String getRepoKey() {
            return repoKey;
        }
    }
}

/*
 * SPDX-Copyright: Copyright (c) Capital One Services, LLC
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 Capital One Services, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
