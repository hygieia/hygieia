package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.GitRepoData;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QGitRepoData;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.GitRepoRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.GitreposRequest;
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
public class GitreposServiceImpl implements GitreposService {

    private final GitRepoRepository repository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public GitreposServiceImpl(GitRepoRepository repository,
                               ComponentRepository componentRepository,
                               CollectorRepository collectorRepository,
                               CollectorService colllectorService) {
        this.repository = repository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = colllectorService;
    }

    @Override
    public DataResponse<Iterable<GitRepoData>> search(GitreposRequest request) {
        QGitRepoData commit = new QGitRepoData("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(commit.collectorItemId.eq(item.getId()));

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(repository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    /*
    @Override
    public String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException {
        GitHubv3 gitHubv3 = new GitHubv3(request.toJSONString());

        if ((gitHubv3.getCollector() == null) || (gitHubv3.getCollectorItem() == null) || (CollectionUtils.isEmpty(gitHubv3.getCommits())))
            throw new HygieiaException("Nothing to update.", HygieiaException.NOTHING_TO_UPDATE);

        Collector col = collectorService.createCollector(gitHubv3.getCollector());
        if (col == null) throw new HygieiaException("Failed creating collector.", HygieiaException.COLLECTOR_CREATE_ERROR);

        CollectorItem item = gitHubv3.getCollectorItem();
        item.setCollectorId(col.getId());
        CollectorItem colItem = collectorService.createCollectorItem(item);
        if (colItem == null) throw new HygieiaException("Failed creating collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        int count = 0;
        for (GitRepoData c : gitHubv3.getCommits()) {
            if (isNewCommit(colItem, c)) {
                c.setCollectorItemId(colItem.getId());
                repository.save(c);
                count = count + 1;
            }
        }
        return col.getName() + ":" + colItem.getId() + ":" + count + " new repo inserted.";

    }

    private boolean isNewCommit(CollectorItem repo, GitRepoData commit) {
        return repository.findByCollectorItemIdAndName(
                repo.getId(), commit.getName()) == null;
    }

    private class GitHubv3 {
        private static final String REPO_URL = "url";
        private static final String BRANCH = "branch";
        private static final String SCM_TAG = "scm";
        private CollectorItem collectorItem;
        private Collector collector;
        private List<GitRepoData> commits = new ArrayList<>();
        private String branch;
        private String url;

        private JSONObject jsonObject;
        JSONParser parser = new JSONParser();


        public GitHubv3(String json) throws ParseException, HygieiaException {

            this.jsonObject = (JSONObject) parser.parse(json);
            buildCommits();
            if (!CollectionUtils.isEmpty(commits)) {
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

        public List<GitRepoData> getCommits() {
            return commits;
        }

        private void buildCommits() throws HygieiaException {

            JSONArray commitArray = (JSONArray) jsonObject.get("commits");
            JSONObject repoObject = (JSONObject) jsonObject.get("repository");
            url = str(repoObject, "url"); // Repo can be null, but ok to throw NPE.
            branch = str(jsonObject, "ref").replace("refs/heads/", ""); //wow!
            if (CollectionUtils.isEmpty(commitArray)) return;
            for (Object c : commitArray) {
                JSONObject cObj = (JSONObject) c;
                JSONObject authorObject = (JSONObject) cObj.get("author");
                String message = str(cObj, "message");
                String author = str(authorObject, "name");
                long timestamp = new DateTime(str(cObj, "timestamp"))
                        .getMillis();
                int numberChanges = ((JSONArray) cObj.get("added")).size() +
                        ((JSONArray) cObj.get("removed")).size() +
                        ((JSONArray) cObj.get("modified")).size();
                GitRepoData commit = new GitRepoData();
                commit.setScmUrl(url);
                commit.setTimestamp(System.currentTimeMillis()); // this is hygieia timestamp.
                commits.add(commit);
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
    */

}
