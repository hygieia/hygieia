package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QIssue;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.IssueRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.IssueRequest;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository,
                           ComponentRepository componentRepository,
                           CollectorRepository collectorRepository,
                           CollectorService colllectorService) {
        this.issueRepository = issueRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = colllectorService;
    }

    @Override
    public DataResponse<Iterable<Issue>> search(IssueRequest request) {
        QIssue issue = new QIssue("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(issue.collectorItemId.eq(item.getId()));

        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(issue.scmCommitTimestamp.goe(endTimeTarget));
        }
        if (!request.getRevisionNumbers().isEmpty()) {
            builder.and(issue.scmRevisionNumber.in(request.getRevisionNumbers()));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(issueRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException {
        GitHubv3 gitHubv3 = new GitHubv3(request.toJSONString());

        if ((gitHubv3.getCollector() == null) || (gitHubv3.getCollectorItem() == null) || (CollectionUtils.isEmpty(gitHubv3.getIssues())))
            throw new HygieiaException("Nothing to update.", HygieiaException.NOTHING_TO_UPDATE);

        Collector col = collectorService.createCollector(gitHubv3.getCollector());
        if (col == null) throw new HygieiaException("Failed creating collector.", HygieiaException.COLLECTOR_CREATE_ERROR);

        CollectorItem item = gitHubv3.getCollectorItem();
        item.setCollectorId(col.getId());
        CollectorItem colItem = collectorService.createCollectorItem(item);
        if (colItem == null) throw new HygieiaException("Failed creating collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        int count = 0;
        for (Issue c : gitHubv3.getIssues()) {
            if (isNewIssue(colItem, c)) {
                c.setCollectorItemId(colItem.getId());
                issueRepository.save(c);
                count = count + 1;
            }
        }
        return col.getId() + ":" + colItem.getId() + ":" + count + " new Issue(s) inserted.";
    }


    private boolean isNewIssue(CollectorItem repo, Issue Issue) {
        return issueRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), Issue.getScmRevisionNumber()) == null;
    }

    private class GitHubv3 {
        private static final String REPO_URL = "url";
        private static final String BRANCH = "branch";
        private static final String SCM_TAG = "scm";
        private CollectorItem collectorItem;
        private Collector collector;
        private List<Issue> Issues = new ArrayList<>();
        private String branch;
        private String url;

        private JSONObject jsonObject;
        JSONParser parser = new JSONParser();


        public GitHubv3(String json) throws ParseException, HygieiaException {

            this.jsonObject = (JSONObject) parser.parse(json);
            buildIssues();
            if (!CollectionUtils.isEmpty(Issues)) {
                buildCollectorItem();
                buildCollector();
            }
        }

        private void buildCollector() {
            collector = new Collector();
            collector.setCollectorType(CollectorType.SCM);
            collector.setLastExecuted(System.currentTimeMillis());
            collector.setOnline(true);
            collector.setEnabled(true);
            collector.setName("GitHub");
        }

        private void buildCollectorItem() {
            if (!StringUtils.isEmpty(branch)) {
                collectorItem = new CollectorItem();
                collectorItem.setEnabled(false);
                collectorItem.setPushed(true);
                collectorItem.setLastUpdated(System.currentTimeMillis());
                collectorItem.getOptions().put(REPO_URL, url);
                collectorItem.getOptions().put(BRANCH, branch);
                collectorItem.getOptions().put(SCM_TAG, "Github");
            }
        }

        public CollectorItem getCollectorItem() {
            return collectorItem;
        }

        public Collector getCollector() {
            return collector;
        }

        public List<Issue> getIssues() {
            return Issues;
        }

        private void buildIssues() throws HygieiaException {

            JSONArray IssueArray = (JSONArray) jsonObject.get("issues");
            JSONObject repoObject = (JSONObject) jsonObject.get("repository");
            url = str(repoObject, "url"); // Repo can be null, but ok to throw NPE.
            branch = str(jsonObject, "ref").replace("refs/heads/", ""); //wow!
            if (CollectionUtils.isEmpty(IssueArray)) return;
            for (Object c : IssueArray) {
                JSONObject cObj = (JSONObject) c;
                JSONObject authorObject = (JSONObject) cObj.get("author");
                String message = str(cObj, "message");
                String author = str(authorObject, "name");
                long timestamp = new DateTime(str(cObj, "timestamp"))
                        .getMillis();
                int numberChanges = ((JSONArray) cObj.get("added")).size() +
                        ((JSONArray) cObj.get("removed")).size() +
                        ((JSONArray) cObj.get("modified")).size();
                Issue Issue = new Issue();
                Issue.setScmUrl(url);
                Issue.setTimestamp(System.currentTimeMillis()); // this is hygieia timestamp.
                Issue.setScmRevisionNumber(str(cObj, "id"));
                Issue.setName(author);
                Issue.setNumberOfChanges(numberChanges);
                Issue.setScmBranch(branch);
                Issues.add(Issue);
            }
        }
        private String str(JSONObject json, String key) throws HygieiaException {
            if (json == null) {
                throw new HygieiaException("Field '" + key + "' cannot be missing or null or empty", HygieiaException.JSON_FORMAT_ERROR);
            }
            Object value = json.get(key);
            return (value == null) ? null : value.toString();
        }

    }
}
