package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.model.QGitRequest;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitRequestRepository;
import com.capitalone.dashboard.request.GitRequestRequest;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GitRequestServiceImpl implements GitRequestService {

    private final GitRequestRepository gitRequestRepository;
    private final ComponentRepository componentRepository;
    private final CollectorRepository collectorRepository;
    private final CollectorService collectorService;

    @Autowired
    public GitRequestServiceImpl(GitRequestRepository gitRequestRepository,
                           ComponentRepository componentRepository,
                           CollectorRepository collectorRepository,
                                 CollectorService collectorService) {
        this.gitRequestRepository = gitRequestRepository;
        this.componentRepository = componentRepository;
        this.collectorRepository = collectorRepository;
        this.collectorService = collectorService;
    }

    @Override
    public DataResponse<Iterable<GitRequest>> search(GitRequestRequest request,
                                                     String type, String state) {
        QGitRequest gitRequest = new QGitRequest("search");
        BooleanBuilder builder = new BooleanBuilder();

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.SCM).get(0);

        if (item == null) {
            Iterable<GitRequest> results = new ArrayList<>();
            return new DataResponse<>(results, new Date().getTime());
        }

        builder.and(gitRequest.collectorItemId.eq(item.getId()));
        if (request.getNumberOfDays() != null) {
            long endTimeTarget = new LocalDate().minusDays(request.getNumberOfDays()).toDate().getTime();
            builder.and(gitRequest.timestamp.goe(endTimeTarget));
        }
        if ( (type != null) &&
                ((type.toLowerCase().equals("pull")) || (type.toLowerCase().equals("issue")))) {
            builder.and(gitRequest.requestType.eq(type));
        }
        if ( (state != null) &&
                ((state.toLowerCase().equals("open")) ||
                (state.toLowerCase().equals("closed")) || (state.toLowerCase().equals("merged")))) {
            builder.and(gitRequest.state.eq(state));
        }
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        if ((collector == null) || (collector.getId() == null)) {
            Iterable<GitRequest> results = new ArrayList<>();
            return new DataResponse<>(results, new Date().getTime());
        }
        return new DataResponse<>(gitRequestRepository.findAll(builder.getValue()), collector.getLastExecuted());
    }
    @Override
    public String createFromGitHubv3(JSONObject request) throws ParseException, HygieiaException {
        GitRequestServiceImpl.GitHubv3 gitHubv3 = new GitRequestServiceImpl.GitHubv3(request.toJSONString());

        if ((gitHubv3.getCollector() == null) || (gitHubv3.getCollectorItem() == null) || (CollectionUtils.isEmpty(gitHubv3.getGitRequests())))
            throw new HygieiaException("Nothing to update.", HygieiaException.NOTHING_TO_UPDATE);

        Collector col = collectorService.createCollector(gitHubv3.getCollector());
        if (col == null) throw new HygieiaException("Failed creating collector.", HygieiaException.COLLECTOR_CREATE_ERROR);

        CollectorItem item = gitHubv3.getCollectorItem();
        item.setCollectorId(col.getId());
        CollectorItem colItem = collectorService.createCollectorItem(item);
        if (colItem == null) throw new HygieiaException("Failed creating collector item.", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);

        int count = 0;
        for (GitRequest c : gitHubv3.getGitRequests()) {
            if (isNewGitRequest(colItem, c)) {
                c.setCollectorItemId(colItem.getId());
                gitRequestRepository.save(c);
                count = count + 1;
            }
        }
        return col.getId() + ":" + colItem.getId() + ":" + count + " new gitRequest(s) inserted.";

    }


    private boolean isNewGitRequest(CollectorItem repo, GitRequest gitRequest) {
        return gitRequestRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo.getId(), gitRequest.getScmRevisionNumber()) == null;
    }

    private class GitHubv3 {
        private static final String REPO_URL = "url";
        private static final String BRANCH = "branch";
        private static final String SCM_TAG = "scm";
        private CollectorItem collectorItem;
        private Collector collector;
        private List<GitRequest> gitRequests = new ArrayList<>();
        private String branch;
        private String url;

        private JSONObject jsonObject;
        private JSONParser parser = new JSONParser();


        public GitHubv3(String json) throws ParseException, HygieiaException {

            this.jsonObject = (JSONObject) parser.parse(json);
            buildGitRequests();
            if (!CollectionUtils.isEmpty(gitRequests)) {
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

        public List<GitRequest> getGitRequests() {
            return gitRequests;
        }

        private void buildGitRequests() throws HygieiaException {
            GitRequest gitRequest = new GitRequest();
            // Both Pull and Issue Events can be handled here
            JSONObject  reqObject = (JSONObject) jsonObject.get("pull_request");
            gitRequest.setRequestType("pull");
            if (reqObject == null) {
                reqObject = (JSONObject) jsonObject.get("issue");
                gitRequest.setRequestType("issue");
            }

            if ( reqObject == null) {
                return;
            }
            JSONObject senderObject = (JSONObject) jsonObject.get("sender");
            JSONObject repoObject = (JSONObject) jsonObject.get("repository");
            String url = str(repoObject, "url"); // Repo can be null, but ok to throw NPE.
            gitRequest.setScmUrl(url);
            //Pulls and Issues are always on master
            gitRequest.setScmBranch("master");

            long timestamp = System.currentTimeMillis();
            gitRequest.setTimestamp(System.currentTimeMillis()); // this is hygieia timestamp.
            gitRequest.setScmRevisionNumber(str(reqObject,"number"));
            gitRequest.setScmAuthor(str(senderObject, "login"));
            gitRequest.setUserId(str(senderObject, "login"));
            gitRequest.setScmCommitLog(str(reqObject, "title"));
            gitRequest.setCreatedAt(new DateTime(str(reqObject,"created_at")).getMillis());
            gitRequest.setClosedAt(new DateTime(str(reqObject,"closed_at")).getMillis());
            gitRequest.setMergedAt(new DateTime(str(reqObject,"merged_at")).getMillis());
            gitRequest.setState(str(reqObject,"state"));
            gitRequest.setNumber(str(reqObject,"number"));
            String orgRepo = str(repoObject,"full_name");
            if (orgRepo != null) {
                String reponameArray[] = orgRepo.split("/");
                if ((reponameArray != null) && ( reponameArray.length > 1)) {
                    gitRequest.setOrgName(reponameArray[0]);
                    gitRequest.setRepoName(reponameArray[1]);
                }
            }

            JSONObject headObject = (JSONObject) jsonObject.get("head");
            JSONObject headRepoObject = (JSONObject) headObject.get("repo");
            gitRequest.setSourceBranch(str(headObject, "ref"));
            gitRequest.setSourceRepo(str(headRepoObject, "full_name"));
            gitRequest.setHeadSha(str(headObject, "sha"));

            JSONObject baseObject = (JSONObject) jsonObject.get("base");
            JSONObject baseRepoObject = (JSONObject) baseObject.get("repo");
            gitRequest.setTargetBranch(str(baseObject, "ref"));
            gitRequest.setTargetRepo(str(baseRepoObject, "full_name"));
            gitRequest.setBaseSha(str(baseObject, "sha"));

        }
        private String str(JSONObject json, String key) throws HygieiaException {
            if (json == null) {
                throw new HygieiaException("Field '" + key + "' cannot be missing or null or empty",
                        HygieiaException.JSON_FORMAT_ERROR);
            }
            Object value = json.get(key);
            return (value == null) ? null : value.toString();
        }

    }

  }
