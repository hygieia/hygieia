package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.model.PipelineResponseCommit;
import com.capitalone.dashboard.model.PipelineStage;
import com.capitalone.dashboard.model.PipelineStageType;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.util.PipelineUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("pipeline")
public class PipelineServiceImpl implements PipelineService {

    private static final int PROD_COMMIT_DATE_RANGE_DEFAULT = -90;
    private final PipelineRepository pipelineRepository;
    private final DashboardRepository dashboardRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final ApiSettings settings;

    @Autowired
    public PipelineServiceImpl(PipelineRepository pipelineRepository, DashboardRepository dashboardRepository, CollectorItemRepository collectorItemRepository,
                               ApiSettings settings) {
        this.pipelineRepository = pipelineRepository;
        this.dashboardRepository = dashboardRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.settings = settings;
    }

    @Override
    public Iterable<PipelineResponse> search(PipelineSearchRequest searchRequest) {
        List<PipelineResponse> pipelineResponses = new ArrayList<>();
        for(ObjectId collectorItemId : searchRequest.getCollectorItemId()){
            Pipeline pipeline = getOrCreatePipeline(collectorItemId);
            pipelineResponses.add(buildPipelineResponse(pipeline, searchRequest.getBeginDate(), searchRequest.getEndDate()));
        }
        return pipelineResponses;
    }

    protected Pipeline getOrCreatePipeline(ObjectId collectorItemId) {
        Pipeline pipeline = pipelineRepository.findByCollectorItemId(collectorItemId);
        if(pipeline == null){
            pipeline = new Pipeline();
            pipeline.setCollectorItemId(collectorItemId);
            pipelineRepository.save(pipeline);
        }
        return pipeline;
    }

    private PipelineResponse buildPipelineResponse(Pipeline pipeline, Long beginDate, Long endDate){
        Long lowerBound = beginDate;
        if(beginDate == null){
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, PROD_COMMIT_DATE_RANGE_DEFAULT);
            lowerBound = cal.getTime().getTime();
        }
        Long upperBound = endDate != null ? endDate : new Date().getTime();
        /**
         * get the collector item and dashboard
         */
        CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
        Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));
        PipelineResponse pipelineResponse = new PipelineResponse();
        pipelineResponse.setCollectorItemId(dashboardCollectorItem.getId());
        pipelineResponse.setProdStage(PipelineUtils.getProdStage(dashboard));
        pipelineResponse.setOrderMap(PipelineUtils.getOrderForStages(dashboard));

        /**
         * iterate over the pipeline stages
         * **/
        Map<PipelineStage, String> stageToEnvironmentNameMap = PipelineUtils.getStageToEnvironmentNameMap(dashboard);
        List<PipelineStage> pipelineStageList = new ArrayList<>();
        for (PipelineStage pl : stageToEnvironmentNameMap.keySet()) {
            pipelineStageList.add(pl);
        }
        Map<String,String> orderMap = pipelineResponse.getOrderMap();
        for (Map.Entry<String, String> entry : orderMap.entrySet())
            {
                String stageName = entry.getValue();
                for(PipelineStage stage : pipelineStageList){
                    if(stageName.equalsIgnoreCase(stage.getName())) {
                        List<PipelineResponseCommit> commitsForStage = findNotPropagatedCommits(dashboard, pipeline, stage, pipelineStageList,orderMap);
                        pipelineResponse.setStageCommits(stage, commitsForStage);
                        /**
                         * remove prod commits outside of filter date range
                         */
                        Iterator<PipelineResponseCommit> commitIterator = commitsForStage.iterator();
                        if (stage.equals(pipelineResponse.getProdStage())) {
                            while (commitIterator.hasNext()) {
                                PipelineResponseCommit commit = commitIterator.next();
                                if (!isBetween(commit.getProcessedTimestamps().get(stage.getName()), lowerBound, upperBound)) {
                                    commitIterator.remove();
                                }
                            }
                        }
                    }
                }
            }
        pipelineResponse.setUnmappedStages(findUnmappedStages(dashboard,pipelineStageList)
                .stream().map(it -> it.getName()).collect(Collectors.toList()));

        return pipelineResponse;
    }

    /**
     * finds any stages for a dashboard that aren't mapped.
     * @param dashboard
     * @return a list of deploy PipelineStages that are not mapped
     */
    private List<PipelineStage> findUnmappedStages(Dashboard dashboard,List<PipelineStage> pipelineStageList){
        List<PipelineStage> unmappedStages = new ArrayList<>();

        Map<PipelineStage, String> stageToEnvironmentNameMap = PipelineUtils.getStageToEnvironmentNameMap(dashboard);

        for (PipelineStage systemStage : pipelineStageList) {
            if (PipelineStageType.DEPLOY.equals(systemStage.getType())) {
                String mappedName = stageToEnvironmentNameMap.get(systemStage);
                if (mappedName == null || mappedName.isEmpty()) {
                    unmappedStages.add(systemStage);
                }
            }
        }

        return unmappedStages;
    }

    /**
     * Finds a map of commits for all stages after the current stage
     * @param stage
     * @param pipeline
     * @param dashboard
     * @return
     */
    private Map<String, PipelineCommit> getCommitsAfterStage(PipelineStage stage, Pipeline pipeline, Dashboard dashboard,List<PipelineStage> pipelineStageList,Map<String,String> orderMap){
        Map<String, PipelineCommit> unionOfAllSets = new HashMap<>();
        // get key(ordinal) for stage name
        List<String> list = getKeysByValue(orderMap,stage.getName());
        int ordinal = Integer.parseInt(list.get(0));
        for (int systemStageOrdinal = ordinal+1; systemStageOrdinal < pipelineStageList.size(); ++systemStageOrdinal) {
            PipelineStage systemStage = null;
            for (Map.Entry<String, String> entry : orderMap.entrySet()) {
                String stageOrder = entry.getKey();
                String stageName = entry.getValue();
                if(Integer.parseInt(stageOrder) == systemStageOrdinal){
                    for(PipelineStage currentStage : pipelineStageList){
                        if(stageName.equalsIgnoreCase(currentStage.getName())){
                             systemStage = currentStage;
                        }
                    }
                }
            }
            Map<String, PipelineCommit> commits = findCommitsForStage(dashboard, pipeline, systemStage);
            unionOfAllSets.putAll(commits);
        }
        return unionOfAllSets;
    }


    private  <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean isBetween(Long commitTimestamp, Long lowerBound, Long upperBound){
        return (lowerBound <= commitTimestamp && commitTimestamp <= upperBound);
    }

    /**
     * For a given commit, will traverse the pipeline and find the time it entered in each stage of the pipeline
     * @param commit
     * @param dashboard
     * @param pipeline
     * @return
     */
    private PipelineResponseCommit applyStageTimestamps(PipelineResponseCommit commit, Dashboard dashboard, Pipeline pipeline,List<PipelineStage> pipelineStageList){
        PipelineResponseCommit returnCommit = new PipelineResponseCommit(commit);

        for(PipelineStage systemStage : pipelineStageList) {
            //get commits for a given stage
            Map<String, PipelineCommit> commitMap = findCommitsForStage(dashboard, pipeline, systemStage);

            //if this commit doesnt have a processed timestamp for this stage, add one
            PipelineCommit pipelineCommit = commitMap.get(commit.getScmRevisionNumber());
            if(pipelineCommit != null && !returnCommit.getProcessedTimestamps().containsKey(systemStage.getName())){
                Long timestamp = pipelineCommit.getTimestamp();
                returnCommit.addNewPipelineProcessedTimestamp(systemStage, timestamp);
            }
        }
        return returnCommit;
    }

    /**
     * Gets all commits for a given pipeline stage, taking into account the mappings for environment stages
     * @param dashboard
     * @param pipeline
     * @param stage
     * @return
     */
    private Map<String, PipelineCommit> findCommitsForStage(Dashboard dashboard, Pipeline pipeline, PipelineStage stage) {
        Map<String, PipelineCommit> commitMap = new HashMap<>();

        String pseudoEnvironmentName =
                PipelineStage.COMMIT.equals(stage) || PipelineStage.BUILD.equals(stage)? stage.getName() :
                        PipelineUtils.getStageToEnvironmentNameMap(dashboard).get(stage);

        if(pseudoEnvironmentName != null){
            commitMap = pipeline.getCommitsByEnvironmentName(pseudoEnvironmentName);
        }
        return commitMap;
    }

    /**
     * get the commits for a given stage by finding which commits havent passed to a later stage
     * @param dashboard dashboard
     * @param pipeline pipeline for that dashboard
     * @param stage current stage
     * @return a list of all commits as pipeline response commits that havent moved past the current stage
     */
    public List<PipelineResponseCommit> findNotPropagatedCommits(Dashboard dashboard, Pipeline pipeline, PipelineStage stage,List<PipelineStage> pipelineStageList,Map<String,String> orderMap){
        Map<String, PipelineCommit> startingStage = findCommitsForStage(dashboard, pipeline, stage);
        Map<String, PipelineCommit> commitsInLaterStages = getCommitsAfterStage(stage, pipeline, dashboard,pipelineStageList,orderMap);

        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry<String,PipelineCommit> entry : startingStage.entrySet()){
            if(!commitsInLaterStages.containsKey(entry.getKey())) {
                PipelineResponseCommit commit = applyStageTimestamps(new PipelineResponseCommit((PipelineCommit) entry.getValue()), dashboard, pipeline, pipelineStageList);
                notPropagatedCommits.add(commit);
            }
        }
        return notPropagatedCommits;
    }
}
