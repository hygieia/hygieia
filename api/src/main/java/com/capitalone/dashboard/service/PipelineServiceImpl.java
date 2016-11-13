package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("pipeline")
public class PipelineServiceImpl implements PipelineService {

    private static final int PROD_COMMIT_DATE_RANGE_DEFAULT = -90;
    private final PipelineRepository pipelineRepository;
    private final DashboardRepository dashboardRepository;
    private final CollectorItemRepository collectorItemRepository;

    @Autowired
    public PipelineServiceImpl(PipelineRepository pipelineRepository, DashboardRepository dashboardRepository, CollectorItemRepository collectorItemRepository) {
        this.pipelineRepository = pipelineRepository;
        this.dashboardRepository = dashboardRepository;
        this.collectorItemRepository = collectorItemRepository;
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

        //sets the lower and upper bound for the prod bucket's commits.  uses constant for lower bound limit and today as default for upper bound
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

        /**
         * iterate over the pipeline stages (which are ordered as defined in the enum)
         * **/
        for(PipelineStageType stage : PipelineStageType.values()){

            List<PipelineResponseCommit> commitsForStage = findNotPropagatedCommits(dashboard, pipeline, stage);
            pipelineResponse.getStages().put(stage, commitsForStage);
            /**
             * remove prod commits outside of filter date range
             */
            Iterator<PipelineResponseCommit> commitIterator = commitsForStage.iterator();
            if(stage.equals(PipelineStageType.Prod)){
                while(commitIterator.hasNext()){
                    PipelineResponseCommit commit = commitIterator.next();
                    if(!isBetween(commit.getProcessedTimestamps().get(stage.name()), lowerBound, upperBound)){
                        commitIterator.remove();
                    }
                }
            }
        }

        pipelineResponse.setUnmappedStages(findUnmappedEnvironments(dashboard));
        return pipelineResponse;
    }

    /**
     * finds any environments for a dashboard that aren't mapped.
     * @param dashboard
     * @return
     */
    private List<PipelineStageType> findUnmappedEnvironments(Dashboard dashboard){


        Map<String, String> environmentMappings= new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                HashMap<?,?> gh = (HashMap<?,?>) widget.getOptions().get("mappings");
                for (Map.Entry<?, ?> entry : gh.entrySet()) {
                    environmentMappings.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }

        List<PipelineStageType> unmappedNames = new ArrayList<>();
        for(PipelineStageType stage : PipelineStageType.values()){
            if(!stage.equals(PipelineStageType.Build) && !stage.equals(PipelineStageType.Commit)){
                String mappedName = environmentMappings.get(stage.name());
                if(mappedName == null || mappedName.isEmpty()){
                    unmappedNames.add(stage);
                }
            }

        }

        return unmappedNames;
    }

    /**
     * Finds a map of commits for all stages after the current stage
     * @param stage
     * @param pipeline
     * @param dashboard
     * @return
     */
    private Map<String, PipelineCommit> getCommitsAfterStage(PipelineStageType stage, Pipeline pipeline, Dashboard dashboard){
        Map<String, PipelineCommit> unionOfAllSets = new HashMap<>();
        for(PipelineStageType stageType : PipelineStageType.values()){
            if(stageType.ordinal() > stage.ordinal()){
                Map<String, PipelineCommit> commits = findCommitsForPipelineStageType(dashboard, pipeline, stageType);
                unionOfAllSets.putAll(commits);
            }
        }
        return unionOfAllSets;
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
    private PipelineResponseCommit applyStageTimestamps(PipelineResponseCommit commit, Dashboard dashboard, Pipeline pipeline){
        PipelineResponseCommit returnCommit = new PipelineResponseCommit(commit);


        for(PipelineStageType stageType : PipelineStageType.values()){
            //get commits for a given stage
            Map<String, PipelineCommit> commitMap = findCommitsForPipelineStageType(dashboard, pipeline, stageType);

            //if this commit doesnt have a processed timestamp for this stage, add one
            PipelineCommit pipelineCommit = commitMap.get(commit.getScmRevisionNumber());
            if(pipelineCommit != null && !returnCommit.getProcessedTimestamps().containsKey(stageType.name())){
                Long timestamp = pipelineCommit.getTimestamp();
                returnCommit.addNewPipelineProcessedTimestamp(stageType.name(), timestamp);
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
    private Map<String, PipelineCommit> findCommitsForPipelineStageType(Dashboard dashboard, Pipeline pipeline, PipelineStageType stageType) {
        String mappedName = (stageType.equals(PipelineStageType.Build) || stageType.equals(PipelineStageType.Commit)) ? stageType.name() : dashboard.findEnvironmentMappings().get(stageType);
        Map<String, PipelineCommit> commitMap = new HashMap<>();
        if(mappedName != null){
            commitMap = pipeline.getCommitsByStage(mappedName);
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
    public List<PipelineResponseCommit> findNotPropagatedCommits(Dashboard dashboard, Pipeline pipeline, PipelineStageType stage){

        Map<String, PipelineCommit> startingStage = findCommitsForPipelineStageType(dashboard, pipeline, stage);
        Map<String, PipelineCommit> commitsInLaterStages = getCommitsAfterStage(stage, pipeline, dashboard);

        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry<String,PipelineCommit> entry : startingStage.entrySet()){
            if(!commitsInLaterStages.containsKey(entry.getKey())){
                PipelineResponseCommit commit = applyStageTimestamps(new PipelineResponseCommit((PipelineCommit)entry.getValue()), dashboard, pipeline);
                notPropagatedCommits.add(commit);
            }
        }
        return notPropagatedCommits;
    }



}
