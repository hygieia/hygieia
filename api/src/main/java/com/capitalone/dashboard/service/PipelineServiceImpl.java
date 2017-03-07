package com.capitalone.dashboard.service;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.capitalone.dashboard.util.PipelineUtils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

        if(beginDate == null){
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, PROD_COMMIT_DATE_RANGE_DEFAULT);
        }
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
        for(PipelineStage stage : pipelineStageList){
            List<PipelineResponseCommit> commitsForStage = findNotPropagatedCommits(dashboard, pipeline, stage,pipelineStageList);
            pipelineResponse.setStageCommits(stage, commitsForStage);
        }
        return pipelineResponse;
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
     * @param stageType
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
    public List<PipelineResponseCommit> findNotPropagatedCommits(Dashboard dashboard, Pipeline pipeline, PipelineStage stage,List<PipelineStage> pipelineStageList){
        Map<String, PipelineCommit> startingStage = findCommitsForStage(dashboard, pipeline, stage);
        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry<String,PipelineCommit> entry : startingStage.entrySet()){
            PipelineResponseCommit commit = applyStageTimestamps(new PipelineResponseCommit((PipelineCommit)entry.getValue()), dashboard, pipeline,pipelineStageList);
            notPropagatedCommits.add(commit);
        }
        return notPropagatedCommits;
    }
}
