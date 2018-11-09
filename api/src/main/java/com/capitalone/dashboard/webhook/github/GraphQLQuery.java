package com.capitalone.dashboard.webhook.github;

public class GraphQLQuery {
    public static final String COMMITS_GRAPHQL = "query ($owner: String!, $name: String!, $branch: String!, $since: GitTimestamp!) {" +
    "    repository(owner: $owner, name: $name) {" +
    "        ref(qualifiedName: $branch) {" +
    "            target {" +
    "               ... on Commit {" +
    "                    history(first: 100, since: $since) {" +
    "                        edges {" +
    "                            cursor" +
    "                            node {" +
    "                                oid" +
    "                                author {" +
    "                                   name" +
    "                                   date" +
    "                                   user {" +
    "                                       login" +
    "                                   }" +
    "                                }" +
    "                                committer {" +
    "                                   name" +
    "                                   date" +
    "                                   user {" +
    "                                       login" +
    "                                   }" +
    "                                }" +
    "                                parents(first: 10) {" +
    "                                    nodes {" +
    "                                        oid" +
    "                                    }" +
    "                                }" +
    "                            }" +
    "                        }" +
    "                    }" +
    "                }" +
    "            }" +
    "        }" +
    "    }" +
    "}";

    public static final String PR_GRAPHQL_BEGIN_PRE =
            "query ($owner: String!, $name: String!, $number: Int!";

    public static final String PR_GRAPHQL_COMMITS_BEGIN = ", $commits: Int!";

    public static final String PR_GRAPHQL_COMMENTS_BEGIN = ", $comments: Int!";

    public static final String PR_GRAPHQL_BEGIN_POST =
            ") {" +
            "  repository(owner: $owner, name: $name) {" +
            "    pullRequest(number: $number) {";

    public static final String PR_GRAPHQL_COMMITS =
            "      commits(first: $commits) {" +
                    "        totalCount" +
                    "        nodes {" +
                    "          commit {" +
                    "            oid" +
                    "            committedDate" +
                    "            message" +
                    "            status {" +
                    "              context(name: \"approvals/lgtmeow\") {" +
                    "                state" +
                    "                targetUrl" +
                    "                description" +
                    "                context" +
                    "              }" +
                    "            }" +
                    "            author {" +
                    "              name" +
                    "              date" +
                    "              user {" +
                    "                login" +
                    "              }" +
                    "            }" +
                    "          }" +
                    "        }" +
                    "      }";

    public static final String PR_GRAPHQL_COMMENTS =
            "      comments(first: $comments) {" +
                    "        totalCount" +
                    "        nodes {" +
                    "          bodyText" +
                    "          author {" +
                    "            login" +
                    "          }" +
                    "          createdAt" +
                    "          updatedAt" +
                    "        }" +
                    "      }";

    public static final String PR_GRAPHQL_REVIEWS =
            "      reviews(first: 100) {" +
                    "        totalCount" +
                    "        nodes {" +
                    "          id" +
                    "          bodyText" +
                    "          state" +
                    "          author {" +
                    "            login" +
                    "          }" +
                    "          createdAt" +
                    "          updatedAt" +
                    "        }" +
                    "      }";

    public static final String PR_GRAPHQL_END =
            "    }" +
            "  }" +
            "}";
}