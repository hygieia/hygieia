package com.capitalone.dashboard.webhook.github;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.client.RestClient;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.service.CollectorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

import static com.capitalone.dashboard.webhook.github.GitHubPayloadType.Unknown;

@Service
public class GitHubHookServiceImpl implements GitHubHookService {
    private static final Log LOG = LogFactory.getLog(GitHubHookServiceImpl.class);

    private final CommitRepository commitRepository;
    private final GitRequestRepository gitRequestRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final CollectorService collectorService;
    protected final ApiSettings apiSettings;
    protected final RestClient restClient;

    @Autowired
    public GitHubHookServiceImpl(CommitRepository commitRepository,
                                 GitRequestRepository gitRequestRepository,
                                 CollectorService collectorService,
                                 CollectorItemRepository collectorItemRepository,
                                 ApiSettings apiSettings,
                                 RestClient restClient) {
        this.commitRepository = commitRepository;
        this.gitRequestRepository = gitRequestRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.collectorService = collectorService;
        this.apiSettings = apiSettings;
        this.restClient = restClient;
    }

    protected GitHubPayloadType getPayLoadType(JSONObject jsonObject) {
        if (jsonObject.get("commits") != null) { return GitHubPayloadType.Push; }

        if (jsonObject.get("pull_request") != null) { return GitHubPayloadType.PullRequest; }

        if (jsonObject.get("issue") != null) { return GitHubPayloadType.Issues; }

        return Unknown;
    }

    @Override
    public String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException, MalformedURLException {
        GitHubPayloadType payloadType = getPayLoadType(request);
        GitHubV3 gitHubv3 = null;
        String result = null;

        switch (payloadType) {
            case Push:
                gitHubv3 = new GitHubCommitV3(collectorService, restClient, commitRepository, gitRequestRepository, collectorItemRepository, apiSettings);
                break;

            case PullRequest:
                gitHubv3 = new GitHubPullRequestV3(collectorService, restClient, gitRequestRepository, commitRepository, collectorItemRepository, apiSettings);
                break;

            case Issues:
                gitHubv3 = new GitHubIssueV3(collectorService, restClient, gitRequestRepository, collectorItemRepository, apiSettings);
                break;

            default:
                return Unknown + "Request Type";
        }

        long begin = System.currentTimeMillis();

        result = gitHubv3.process(request);

        long end = System.currentTimeMillis();

        LOG.info("Total Time Taken = "+(end-begin)+" milliseconds");

        return result;
    }
}