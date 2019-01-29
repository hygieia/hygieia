package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.model.webhook.github.GitHubParsed;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.service.CollectorService;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.util.Map;

public class GitHubIssueV3 extends GitHubV3 {
    private final  GitRequestRepository gitRequestRepository;
    private final CollectorItemRepository collectorItemRepository;

    public GitHubIssueV3(CollectorService collectorService,
                         RestClient restClient,
                         GitRequestRepository gitRequestRepository,
                         CollectorItemRepository collectorItemRepository,
                         ApiSettings apiSettings) {
        super(collectorService, restClient, apiSettings);
        this.gitRequestRepository =  gitRequestRepository;
        this.collectorItemRepository = collectorItemRepository;
    }

    @Override
    public CollectorItemRepository getCollectorItemRepository() { return this.collectorItemRepository; }

    @Override
    public String process(JSONObject jsonObject) throws MalformedURLException, HygieiaException {
        String result = "Issues Processed Successfully";

        Object issueObject = jsonObject.get("issue");

        if (!(issueObject instanceof Map)) {
            return "Issue Data Not Found";
        }

        Map issueMap = (Map) issueObject;

        Object repoMap = jsonObject.get("repository");
        if (repoMap == null) { return "Repository Data Not Available"; }

        String repoUrl = restClient.getString(repoMap, "html_url");
        GitHubParsed gitHubParsed = new GitHubParsed(repoUrl);

        // Picking the "default_branch" here, didn't find specific branch information on the github webhook issue payload.
        // Also, an issue is seen at the repo-level across all the branches on github, it is not attached a specific branch.
        String branch = restClient.getString(repoMap, "default_branch");

        GitRequest issue = getIssue(issueMap, gitHubParsed, branch);

        gitRequestRepository.save(issue);

        return result;
    }

    protected GitRequest getIssue(Map issueMap, GitHubParsed gitHubParsed, String branch) throws HygieiaException, MalformedURLException {
        if (issueMap.isEmpty()) { return null; }

        GitRequest issue = new GitRequest();
        issue.setRequestType("issue");
        issue.setScmUrl(gitHubParsed.getUrl());
        issue.setScmBranch(branch);
        issue.setOrgName(gitHubParsed.getOrgName());
        issue.setRepoName(gitHubParsed.getRepoName());
        issue.setTimestamp(System.currentTimeMillis());

        String number = restClient.getString(issueMap, "number");
        issue.setScmRevisionNumber(number);
        issue.setNumber(number);

        String message = restClient.getString(issueMap, "title");
        issue.setScmCommitLog(message);

        Object userObject = issueMap.get("user");
        String name = restClient.getString(userObject, "login");
        issue.setUserId(name);

        String created = restClient.getString(issueMap, "created_at");
        long createdTimestamp = new DateTime(created).getMillis();
        issue.setCreatedAt(createdTimestamp);

        String updated = restClient.getString(issueMap, "updated_at");
        long updatedTimestamp = new DateTime(updated).getMillis();
        issue.setUpdatedAt(updatedTimestamp);

        issue.setClosedAt(0);
        issue.setResolutiontime(0);
        issue.setMergedAt(0);
        issue.setState("open");
        String state = restClient.getString(issueMap, "state");
        if ("CLOSED".equalsIgnoreCase(state)) {
            String closed = restClient.getString(issueMap, "closed_at");
            long closedTimestamp = new DateTime(closed).getMillis();
            issue.setScmCommitTimestamp(closedTimestamp);
            issue.setClosedAt(closedTimestamp);
            issue.setMergedAt(closedTimestamp);
            issue.setResolutiontime((closedTimestamp - createdTimestamp));
            issue.setState("closed");
        }

        setCollectorItemId(issue);

        return issue;
    }

    protected void setCollectorItemId(GitRequest issue) throws HygieiaException, MalformedURLException {
        GitRequest existingIssue
                = gitRequestRepository.findByScmUrlIgnoreCaseAndScmBranchIgnoreCaseAndNumberAndRequestTypeIgnoreCase(issue.getScmUrl(), issue.getScmBranch(), issue.getNumber(), "issue");
        if (existingIssue != null) {
            issue.setId(existingIssue.getId());
            issue.setCollectorItemId(existingIssue.getCollectorItemId());
            CollectorItem collectorItem = collectorService.getCollectorItem(existingIssue.getCollectorItemId());
            collectorItem.setEnabled(true);
            collectorItem.setPushed(true);
            collectorItemRepository.save(collectorItem);
        } else {
            GitHubParsed gitHubParsed = new GitHubParsed(issue.getScmUrl());
            CollectorItem collectorItem = getCollectorItem(gitHubParsed.getUrl(), issue.getScmBranch());
            issue.setCollectorItemId(collectorItem.getId());
        }
    }
}