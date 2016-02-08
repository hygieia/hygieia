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

@Service
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
        for (Pipeline pipeline : pipelineRepository.findByCollectorItemIdIn(searchRequest.getCollectorItemId())){
            pipelineResponses.add(buildPipelineResponse(pipeline, searchRequest.getBeginDate(), searchRequest.getEndDate()));
        }
        return pipelineResponses;
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

        CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
        Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));

        PipelineResponse pipelineResponse = new PipelineResponse();
        pipelineResponse.setCollectorItemId(dashboardCollectorItem.getId());

        for(PipelineStageType stage : PipelineStageType.values()){
            List<PipelineResponseCommit> commitsForStage = findNotPropagatedCommits(dashboard, pipeline, stage);
            pipelineResponse.getStages().put(stage, commitsForStage);
            //remove prod commits outside of filter date range
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
        return pipelineResponse;
    }

    //this needs to take into account mapping
    private Map<String, PipelineCommit> getCommitsAfterStage(PipelineStageType stage, Pipeline pipeline, Dashboard dashboard){
        Map<String, PipelineCommit> unionOfAllSets = new HashMap<>();
        for(PipelineStageType stageType : PipelineStageType.values()){
            if(stageType.ordinal() > stage.ordinal()){
                Map<String, PipelineCommit> commits = findCommitsForPipelineStageType(dashboard, pipeline, stage);
                unionOfAllSets.putAll(commits);
            }
        }
        return unionOfAllSets;
    }


    //should pull the setting of begin/end date out of this method
    private boolean isBetween(Long commitTimestamp, Long lowerBound, Long upperBound){
        if(lowerBound <= commitTimestamp && commitTimestamp <= upperBound)
        {
            return true;
        }
        return false;
    }

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

    private Map<String, PipelineCommit> findCommitsForPipelineStageType(Dashboard dashboard, Pipeline pipeline, PipelineStageType stageType) {
        String mappedName = (stageType.equals(PipelineStageType.Build) || stageType.equals(PipelineStageType.Commit)) ? stageType.name() : dashboard.getEnvironmentMappings().get(stageType);
        return pipeline.getCommitsByStage(mappedName);
    }

    public List<PipelineResponseCommit> findNotPropagatedCommits(Dashboard dashboard, Pipeline pipeline, PipelineStageType stage){

        Map<String, PipelineCommit> startingStage = findCommitsForPipelineStageType(dashboard, pipeline, stage);
        Map<String, PipelineCommit> commitsInLaterStages = getCommitsAfterStage(stage, pipeline, dashboard);

        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry entry : startingStage.entrySet()){
            if(!commitsInLaterStages.containsKey(entry.getKey())){
                PipelineResponseCommit commit = applyStageTimestamps(new PipelineResponseCommit((PipelineCommit)entry.getValue()), dashboard, pipeline);
                notPropagatedCommits.add(commit);
            }
        }
        return notPropagatedCommits;
    }



}
