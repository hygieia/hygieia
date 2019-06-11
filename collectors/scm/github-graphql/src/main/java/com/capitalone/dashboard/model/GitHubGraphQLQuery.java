package com.capitalone.dashboard.model;

public class GitHubGraphQLQuery {


    public static final String QUERY_BASE_ALL_FIRST = "query ($owner: String!, $name: String!, $branch: String!, $since: GitTimestamp!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_ALL_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterPull : String!, $afterCommit : String!, $afterIssue : String!, $since: GitTimestamp!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_COMMIT_ONLY_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterCommit : String!, $since: GitTimestamp!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_ISSUE_ONLY_AFTER = "query ($owner: String!, $name: String!, $afterIssue : String!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_PULL_ONLY_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterPull : String!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_ISSUE_AND_PULL_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterIssue : String!, $afterPull : String!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_COMMIT_AND_ISSUE_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterCommit : String!, $afterIssue : String!, $since: GitTimestamp!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";

    public static final String QUERY_BASE_COMMIT_AND_PULL_AFTER = "query ($owner: String!, $name: String!, $branch: String!, $afterPull : String!, $afterCommit : String!, $since: GitTimestamp!, $fetchCount: Int!) {\n" +
            "  repository(owner: $owner, name: $name) {\n";


    public static final String QUERY_COMMIT_HEADER_FIRST = "  ref(qualifiedName: $branch) {\n" +
            "    target {\n" +
            "      ... on Commit {\n" +
            "        history(since: $since, first: $fetchCount) {\n";

    public static final String QUERY_COMMIT_HEADER_AFTER = "  ref(qualifiedName: $branch) {\n" +
            "    target {\n" +
            "      ... on Commit {\n" +
            "        history(since: $since, first: $fetchCount, after: $afterCommit) {\n";


    public static final String QUERY_PULL_HEADER_FIRST = "    pullRequests(first: $fetchCount, baseRefName: $branch, orderBy: {field: UPDATED_AT, direction: DESC}) {\n";

    public static final String QUERY_PULL_HEADER_AFTER = "    pullRequests(first: $fetchCount, after : $afterPull, baseRefName: $branch, orderBy: {field: UPDATED_AT, direction: DESC}) {\n";

    public static final String QUERY_ISSUES_HEADER_FIRST = "    issues(first: $fetchCount, orderBy: {field: UPDATED_AT, direction: DESC}) {\n";

    public static final String QUERY_ISSUES_HEADER_AFTER = "    issues(first: $fetchCount, after : $afterIssue, orderBy: {field: UPDATED_AT, direction: DESC}) {\n";


    public static final String QUERY_COMMIT_MAIN =
            "          pageInfo {\n" +
                    "            endCursor\n" +
                    "            hasNextPage\n" +
                    "          }\n" +
                    "          edges {\n" +
                    "            cursor\n" +
                    "            node {\n" +
                    "              oid\n" +
                    "              changedFiles\n" +
                    "              deletions\n" +
                    "              additions\n" +
                    "              parents(first:10) {\n" +
                    "                nodes {\n" +
                    "                  oid\n" +
                    "                }\n" +
                    "              }\n" +
                    "              message\n" +
                    "                committer {\n" +
                    "                  user {\n" +
                    "                    login\n" +
                    "                  }\n" +
                    "                  name\n" +
                    "                  date\n" +
                    "                }\n" +
                    "                author {\n" +
                    "                  name\n" +
                    "                  user {\n" +
                    "                    login\n" +
                    "                  }\n" +
                    "                  email\n" +
                    "                  date\n" +
                    "                }" +
                    "              status {\n" +
                    "                state\n" +
                    "                contexts {\n" +
                    "                  id\n" +
                    "                  description\n" +
                    "                }\n" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n";


    public static final String QUERY_PULL_MAIN =
            "      totalCount \n" +
                    "      pageInfo {\n" +
                    "        endCursor\n" +
                    "        hasNextPage\n" +
                    "      }\n" +
                    "      edges {\n" +
                    "        cursor\n" +
                    "        node {\n" +
                    "          number\n" +
                    "          mergeable\n" +
                    "          state\n" +
                    "          createdAt\n" +
                    "          updatedAt\n" +
                    "          mergedAt\n" +
                    "          baseRef {\n" +
                    "            target {\n" +
                    "                oid\n" +
                    "            }\n" +
                    "          }\n" +
                    "          baseRefName\n" +
                    "          title\n" +
                    "          mergeCommit {\n" +
                    "            oid\n" +
                    "          }\n" +
                    "          headRepository {\n" +
                    "            name          \n" +
                    "            url          \n" +
                    "          }\n" +
                    "          headRef {\n" +
                    "            target {\n" +
                    "                oid\n" +
                    "            }\n" +
                    "          }\n" +
                    "          headRefName" +
                    "          author {\n" +
                    "            login\n" +
                    "            resourcePath\n" +
                    "          }" +
                    "          commits(first: 250) {\n" +
                    "            totalCount\n" +
                    "            nodes {\n" +
                    "              commit {\n" +
                    "                oid\n" +
                    "                committedDate\n" +
                    "                changedFiles\n" +
                    "                deletions\n" +
                    "                additions\n" +
                    "                message\n" +
                    "                status {\n" +
                    "                  state\n" +
                    "                  contexts {\n" +
                    "                    state\n" +
                    "                    targetUrl\n" +
                    "                    description\n" +
                    "                    context\n" +
                    "                  }\n" +
                    "                }" +
                    "                author {\n" +
                    "                  name\n" +
                    "                  date\n" +
                    "                  user {\n" +
                    "                    login\n" +
                    "                  }\n" +
                    "                }" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "          timeline(last: 100) {\n" +
                    "            edges {\n" +
                    "              node {\n" +
                    "                __typename\n" +
                    "                ... on MergedEvent {\n" +
                    "                  createdAt\n" +
                    "                  commit {\n" +
                    "                    oid\n" +
                    "                  }\n" +
                    "                  pullRequest {\n" +
                    "                    number\n" +
                    "                  }\n" +
                    "                  mergeRefName\n" +
                    "                  actor {\n" +
                    "                    login\n" +
                    "                  }\n" +
                    "                }" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "          comments(first: 100) {\n" +
                    "            totalCount\n" +
                    "            nodes {\n" +
                    "              bodyText\n" +
                    "                author {\n" +
                    "                  login\n" +
                    "                }\n" +
                    "              createdAt \n" +
                    "              updatedAt \n" +
                    "            }\n" +
                    "          }\n" +
                    "          reviews(first: 100) {\n" +
                    "            totalCount\n" +
                    "            nodes {\n" +
                    "              id\n" +
                    "              bodyText\n" +
                    "              state\n" +
                    "                author {\n" +
                    "                  login\n" +
                    "                }\n" +
                    "              createdAt \n" +
                    "              updatedAt \n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n";


    public static final String QUERY_ISSUE_MAIN =
            "      totalCount \n" +
                    "      pageInfo {\n" +
                    "        endCursor\n" +
                    "        hasNextPage\n" +
                    "      }\n" +
                    "      edges {\n" +
                    "        cursor\n" +
                    "        node {\n" +
                    "          number\n" +
                    "          state\n" +
                    "          createdAt\n" +
                    "          updatedAt\n" +
                    "          title\n" +
                    "          author {\n" +
                    "            login\n" +
                    "          }" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n";


    public static final String QUERY_END =
            "  }\n" + "}\n";

    public static final String QUERY_NEW_PR_CHECK ="query ($owner: String!, $name: String!, $branch:String!) {\n" +
            "  repository(owner: $owner, name: $name) {\n" +
            "    pullRequests(first: 1, baseRefName: $branch, orderBy: {field: UPDATED_AT, direction: DESC}) {\n" +
            "      edges {\n" +
            "        node {\n" +
            "          number\n" +
            "          updatedAt\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";


    public static final String QUERY_NEW_ISSUE_CHECK =
            "query ($owner: String!, $name: String!) {\n" +
                    "  repository(owner: $owner, name: $name) {\n" +
                    "    issues(first: 1, orderBy: {field: UPDATED_AT, direction: DESC}) {\n" +
                    "      edges {\n" +
                    "        node {\n" +
                    "          number\n" +
                    "          updatedAt\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";




    public static final String QUERY_RATE_LIMIT = "query {rateLimit {limit remaining resetAt}}";

}