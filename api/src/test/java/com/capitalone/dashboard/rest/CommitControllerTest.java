package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CommitRequest;
import com.capitalone.dashboard.service.CommitService;
import com.capitalone.dashboard.util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class CommitControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private CommitService commitService;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void commit_search() throws Exception {
        Commit commit = makeCommit();
        Iterable<Commit> commits = Arrays.asList(commit);
        DataResponse<Iterable<Commit>> response = new DataResponse<>(commits, 1);

        when(commitService.search(Mockito.any(CommitRequest.class))).thenReturn(response);

        mockMvc.perform(get("/commit?componentId=" + ObjectId.get()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$result", hasSize(1)))
                .andExpect(jsonPath("$result[0].scmUrl", is(commit.getScmUrl())))
                .andExpect(jsonPath("$result[0].scmRevisionNumber", is(commit.getScmRevisionNumber())))
                .andExpect(jsonPath("$result[0].numberOfChanges", is(intVal(commit.getNumberOfChanges()))))
                .andExpect(jsonPath("$result[0].scmCommitTimestamp", is(intVal(commit.getScmCommitTimestamp()))))
                .andExpect(jsonPath("$result[0].scmCommitLog", is(commit.getScmCommitLog())))
                .andExpect(jsonPath("$result[0].scmAuthor", is(commit.getScmAuthor())));
    }

    @Test
    public void  commits_noComponentId_badRequest() throws Exception {
        mockMvc.perform(get("/commit")).andExpect(status().isBadRequest());
    }

    @Test
    public void insertCommitGoodRequest() throws Exception {
        String json = github_push;
        byte[] content = json.getBytes();
        System.out.println(new String(content, StandardCharsets.UTF_8));
        when(commitService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("123456");
        mockMvc.perform(post("/commit/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isCreated());

    }

    @Test
    public void insertCommitBadRequest1() throws Exception {

        byte[] content = "".getBytes();
        System.out.println(new String(content, StandardCharsets.UTF_8));
        when(commitService.createFromGitHubv3(Matchers.any(JSONObject.class))).thenReturn("");
        mockMvc.perform(post("/commit/github/v3")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().isInternalServerError());

    }

    private Commit makeCommit() {
        Commit commit = new Commit();
        commit.setScmUrl("scmUrl");
        commit.setScmRevisionNumber("revNum");
        commit.setNumberOfChanges(20);
        commit.setScmCommitTimestamp(200);
        commit.setScmCommitLog("Log message");
        commit.setScmAuthor("bob");
        commit.setTimestamp(2);
        return commit;
    }

    private int intVal(long value) {
        return Long.valueOf(value).intValue();
    }


    private String getJson(String fileName) throws IOException {
        InputStream inputStream = CommitControllerTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }


    private static final String github_push = "{\n"+
            "  \"ref\": \"refs/heads/changes\",\n"+
            "  \"before\": \"9049f1265b7d61be4a8904a9a27120d2064dab3b\",\n"+
            "  \"after\": \"0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c\",\n"+
            "  \"created\": false,\n"+
            "  \"deleted\": false,\n"+
            "  \"forced\": false,\n"+
            "  \"base_ref\": null,\n"+
            "  \"compare\": \"https://github.com/baxterthehacker/public-repo/compare/9049f1265b7d...0d1a26e67d8f\",\n"+
            "  \"commits\": [\n"+
            "    {\n"+
            "      \"id\": \"0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c\",\n"+
            "      \"distinct\": true,\n"+
            "      \"message\": \"Update README.md\",\n"+
            "      \"timestamp\": \"2015-05-05T19:40:15-04:00\",\n"+
            "      \"url\": \"https://github.com/baxterthehacker/public-repo/commit/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c\",\n"+
            "      \"author\": {\n"+
            "        \"name\": \"baxterthehacker\",\n"+
            "        \"email\": \"baxterthehacker@users.noreply.github.com\",\n"+
            "        \"username\": \"baxterthehacker\"\n"+
            "      },\n"+
            "      \"committer\": {\n"+
            "        \"name\": \"baxterthehacker\",\n"+
            "        \"email\": \"baxterthehacker@users.noreply.github.com\",\n"+
            "        \"username\": \"baxterthehacker\"\n"+
            "      },\n"+
            "      \"added\": [\n"+
            "\n"+
            "      ],\n"+
            "      \"removed\": [\n"+
            "\n"+
            "      ],\n"+
            "      \"modified\": [\n"+
            "        \"README.md\"\n"+
            "      ]\n"+
            "    }\n"+
            "  ],\n"+
            "  \"head_commit\": {\n"+
            "    \"id\": \"0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c\",\n"+
            "    \"distinct\": true,\n"+
            "    \"message\": \"Update README.md\",\n"+
            "    \"timestamp\": \"2015-05-05T19:40:15-04:00\",\n"+
            "    \"url\": \"https://github.com/baxterthehacker/public-repo/commit/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c\",\n"+
            "    \"author\": {\n"+
            "      \"name\": \"baxterthehacker\",\n"+
            "      \"email\": \"baxterthehacker@users.noreply.github.com\",\n"+
            "      \"username\": \"baxterthehacker\"\n"+
            "    },\n"+
            "    \"committer\": {\n"+
            "      \"name\": \"baxterthehacker\",\n"+
            "      \"email\": \"baxterthehacker@users.noreply.github.com\",\n"+
            "      \"username\": \"baxterthehacker\"\n"+
            "    },\n"+
            "    \"added\": [\n"+
            "\n"+
            "    ],\n"+
            "    \"removed\": [\n"+
            "\n"+
            "    ],\n"+
            "    \"modified\": [\n"+
            "      \"README.md\"\n"+
            "    ]\n"+
            "  },\n"+
            "  \"repository\": {\n"+
            "    \"id\": 35129377,\n"+
            "    \"name\": \"public-repo\",\n"+
            "    \"full_name\": \"baxterthehacker/public-repo\",\n"+
            "    \"owner\": {\n"+
            "      \"name\": \"baxterthehacker\",\n"+
            "      \"email\": \"baxterthehacker@users.noreply.github.com\"\n"+
            "    },\n"+
            "    \"private\": false,\n"+
            "    \"html_url\": \"https://github.com/baxterthehacker/public-repo\",\n"+
            "    \"description\": \"\",\n"+
            "    \"fork\": false,\n"+
            "    \"url\": \"https://github.com/baxterthehacker/public-repo\",\n"+
            "    \"forks_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/forks\",\n"+
            "    \"keys_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/keys{/key_id}\",\n"+
            "    \"collaborators_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/collaborators{/collaborator}\",\n"+
            "    \"teams_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/teams\",\n"+
            "    \"hooks_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/hooks\",\n"+
            "    \"issue_events_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/issues/events{/number}\",\n"+
            "    \"events_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/events\",\n"+
            "    \"assignees_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/assignees{/user}\",\n"+
            "    \"branches_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/branches{/branch}\",\n"+
            "    \"tags_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/tags\",\n"+
            "    \"blobs_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/git/blobs{/sha}\",\n"+
            "    \"git_tags_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/git/tags{/sha}\",\n"+
            "    \"git_refs_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/git/refs{/sha}\",\n"+
            "    \"trees_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/git/trees{/sha}\",\n"+
            "    \"statuses_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/statuses/{sha}\",\n"+
            "    \"languages_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/languages\",\n"+
            "    \"stargazers_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/stargazers\",\n"+
            "    \"contributors_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/contributors\",\n"+
            "    \"subscribers_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/subscribers\",\n"+
            "    \"subscription_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/subscription\",\n"+
            "    \"commits_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/commits{/sha}\",\n"+
            "    \"git_commits_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/git/commits{/sha}\",\n"+
            "    \"comments_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/comments{/number}\",\n"+
            "    \"issue_comment_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/issues/comments{/number}\",\n"+
            "    \"contents_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/contents/{+path}\",\n"+
            "    \"compare_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/compare/{base}...{head}\",\n"+
            "    \"merges_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/merges\",\n"+
            "    \"archive_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/{archive_format}{/ref}\",\n"+
            "    \"downloads_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/downloads\",\n"+
            "    \"issues_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/issues{/number}\",\n"+
            "    \"pulls_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/pulls{/number}\",\n"+
            "    \"milestones_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/milestones{/number}\",\n"+
            "    \"notifications_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/notifications{?since,all,participating}\",\n"+
            "    \"labels_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/labels{/name}\",\n"+
            "    \"releases_url\": \"https://api.github.com/repos/baxterthehacker/public-repo/releases{/id}\",\n"+
            "    \"created_at\": 1430869212,\n"+
            "    \"updated_at\": \"2015-05-05T23:40:12Z\",\n"+
            "    \"pushed_at\": 1430869217,\n"+
            "    \"git_url\": \"git://github.com/baxterthehacker/public-repo.git\",\n"+
            "    \"ssh_url\": \"git@github.com:baxterthehacker/public-repo.git\",\n"+
            "    \"clone_url\": \"https://github.com/baxterthehacker/public-repo.git\",\n"+
            "    \"svn_url\": \"https://github.com/baxterthehacker/public-repo\",\n"+
            "    \"homepage\": null,\n"+
            "    \"size\": 0,\n"+
            "    \"stargazers_count\": 0,\n"+
            "    \"watchers_count\": 0,\n"+
            "    \"language\": null,\n"+
            "    \"has_issues\": true,\n"+
            "    \"has_downloads\": true,\n"+
            "    \"has_wiki\": true,\n"+
            "    \"has_pages\": true,\n"+
            "    \"forks_count\": 0,\n"+
            "    \"mirror_url\": null,\n"+
            "    \"open_issues_count\": 0,\n"+
            "    \"forks\": 0,\n"+
            "    \"open_issues\": 0,\n"+
            "    \"watchers\": 0,\n"+
            "    \"default_branch\": \"master\",\n"+
            "    \"stargazers\": 0,\n"+
            "    \"master_branch\": \"master\"\n"+
            "  },\n"+
            "  \"pusher\": {\n"+
            "    \"name\": \"baxterthehacker\",\n"+
            "    \"email\": \"baxterthehacker@users.noreply.github.com\"\n"+
            "  },\n"+
            "  \"sender\": {\n"+
            "    \"login\": \"baxterthehacker\",\n"+
            "    \"id\": 6752317,\n"+
            "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/6752317?v=3\",\n"+
            "    \"gravatar_id\": \"\",\n"+
            "    \"url\": \"https://api.github.com/users/baxterthehacker\",\n"+
            "    \"html_url\": \"https://github.com/baxterthehacker\",\n"+
            "    \"followers_url\": \"https://api.github.com/users/baxterthehacker/followers\",\n"+
            "    \"following_url\": \"https://api.github.com/users/baxterthehacker/following{/other_user}\",\n"+
            "    \"gists_url\": \"https://api.github.com/users/baxterthehacker/gists{/gist_id}\",\n"+
            "    \"starred_url\": \"https://api.github.com/users/baxterthehacker/starred{/owner}{/repo}\",\n"+
            "    \"subscriptions_url\": \"https://api.github.com/users/baxterthehacker/subscriptions\",\n"+
            "    \"organizations_url\": \"https://api.github.com/users/baxterthehacker/orgs\",\n"+
            "    \"repos_url\": \"https://api.github.com/users/baxterthehacker/repos\",\n"+
            "    \"events_url\": \"https://api.github.com/users/baxterthehacker/events{/privacy}\",\n"+
            "    \"received_events_url\": \"https://api.github.com/users/baxterthehacker/received_events\",\n"+
            "    \"type\": \"User\",\n"+
            "    \"site_admin\": false\n"+
            "  }\n"+
            "}";

}
