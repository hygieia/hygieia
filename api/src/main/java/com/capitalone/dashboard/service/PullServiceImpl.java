package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Pull;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.QPull;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.PullRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.request.PullRequest;
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
public class PullServiceImpl implements PullService {

    private final PullRepository PullRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public PullServiceImpl(PullRepository PullRepository,
                            ComponentRepository componentRepository,
                            CollectorRepository collectorRepository,
                            CollectorService colllectorService) {
        this.PullRepository = PullRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = colllectorService;
    }

    @Override
    public DataResponse<Iterable<Pull>> search(PullRequest request) {
        QPull pull = new QPull("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);
        builder.and(pull.collectorItemId.eq(item.getId()));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(pull.scmCommitTimestamp.goe(endTimeTarget));
        }
        if (!request.getRevisionNumbers().isEmpty()) {
            builder.and(pull.scmRevisionNumber.in(request.getRevisionNumbers()));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        return new DataResponse<>(PullRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }

    @Override
    public String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException {
        GitHubv3 gitHubv3 = new GitHubv3(request.toJSONString());

        if ((gitHubv3.getCollector() == null) || (gitHubv3.getCollectorItem() == null) || (CollectionUtils.isEmpty(gitHubv3.getPulls())))
            throw new HygieiaException("Nothing to update.", HygieiaException.NOTHING_TO_UPDATE);

        Collector col = collectorService.createCollector(gitHubv3.getCollector());
        if (col == null) throw new HygieiaException("Failed creating collector.", HygieiaException.COLLECTOR_CREATE_ERROR);

        CollectorItem item = gitHubv3.getCollectorItem();
        item.setCollectorId(col.getId());
        CollectorItem colItem = collectorService.createCollectorItem(item);
        if (colItem == null) throw new HygieiaException("Failed creating collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        int count = 0;
        for (Pull c : gitHubv3.getPulls()) {
            if (isNewPull(colItem, c)) {
                c.setCollectorItemId(colItem.getId());
                PullRepository.save(c);
                count = count + 1;
            }
        }
        return col.getId() + ":" + colItem.getId() + ":" + count + " new Pull(s) inserted.";
    }


    private boolean isNewPull(CollectorItem repo, Pull pull) {
        return PullRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), pull.getScmRevisionNumber()) == null;
    }

    private class GitHubv3 {
        private static final String REPO_URL = "url";
        private static final String BRANCH = "branch";
        private static final String SCM_TAG = "scm";
        private CollectorItem collectorItem;
        private Collector collector;
        private List<Pull> Pulls = new ArrayList<>();
        private String branch;
        private String url;

        private JSONObject jsonObject;
        JSONParser parser = new JSONParser();


        public GitHubv3(String json) throws ParseException, HygieiaException {

            this.jsonObject = (JSONObject) parser.parse(json);
            buildPulls();
            if (!CollectionUtils.isEmpty(Pulls)) {
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

        public List<Pull> getPulls() {
            return Pulls;
        }

        private void buildPulls() throws HygieiaException {

            JSONArray PullArray = (JSONArray) jsonObject.get("pulls");
            JSONObject repoObject = (JSONObject) jsonObject.get("repository");
            url = str(repoObject, "url"); // Repo can be null, but ok to throw NPE.
            branch = str(jsonObject, "ref").replace("refs/heads/", ""); //wow!
            if (CollectionUtils.isEmpty(PullArray)) return;
            for (Object c : PullArray) {
                JSONObject cObj = (JSONObject) c;
                JSONObject authorObject = (JSONObject) cObj.get("author");
                String message = str(cObj, "message");
                String author = str(authorObject, "name");
                long timestamp = new DateTime(str(cObj, "timestamp"))
                        .getMillis();
                int numberChanges = ((JSONArray) cObj.get("added")).size() +
                        ((JSONArray) cObj.get("removed")).size() +
                        ((JSONArray) cObj.get("modified")).size();
                Pull Pull = new Pull();
                Pull.setScmUrl(url);
                Pull.setTimestamp(System.currentTimeMillis()); // this is hygieia timestamp.
                Pull.setScmRevisionNumber(str(cObj, "id"));
                Pull.setName(author);
                Pull.setNumberOfChanges(numberChanges);
                Pull.setScmBranch(branch);
                Pulls.add(Pull);
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
