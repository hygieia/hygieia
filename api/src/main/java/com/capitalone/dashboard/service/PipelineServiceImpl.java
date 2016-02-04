package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.CaseInsensitiveMap;
import org.apache.commons.lang.NotImplementedException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.Pipe;
import java.util.*;

@Service
public class PipelineServiceImpl implements PipelineService {

    private static final int PROD_COMMIT_DATE_RANGE_DEFAULT = 90;
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
            CollectorItem dashboardCollectorItem = collectorItemRepository.findOne(pipeline.getCollectorItemId());
            Dashboard dashboard = dashboardRepository.findOne(new ObjectId((String)dashboardCollectorItem.getOptions().get("dashboardId")));
            pipelineResponses.add(buildPipelineResponse(dashboard, dashboardCollectorItem, pipeline, searchRequest.getBeginDate(), searchRequest.getEndDate()));
        }
        return pipelineResponses;
    }

    private PipelineResponse buildPipelineResponse(Dashboard dashboard, CollectorItem dashboardCollectorItem, Pipeline pipeline, Long beginDate, Long endDate){
        PipelineResponse pipelineResponse = new PipelineResponse();
        pipelineResponse.setCollectorItemId(dashboardCollectorItem.getId());
        Map<PipelineStageType, Map<String, PipelineCommit>> commitsByStage = getCommitsByStage(dashboard, pipeline);

        for(PipelineStageType stage : PipelineStageType.values()){
            List<PipelineResponseCommit> commits = findNotPropagatedCommits(commitsByStage.get(stage), getCommitsAfterStage(stage, commitsByStage), dashboard, pipeline);
            pipelineResponse.getStages().put(stage, commits);
            Iterator<PipelineResponseCommit> commitIterator = commits.iterator();
            if(stage.equals(PipelineStageType.Prod)){
                while(commitIterator.hasNext()){
                    PipelineResponseCommit commit = commitIterator.next();
                    if(!isBetween(commit.getScmCommitTimestamp(), beginDate, endDate)){
                        commitIterator.remove();
                    }
                }
            }
        }
        return pipelineResponse;
    }

    private boolean isBetween(Long commitTimestamp, Long lowerBound, Long upperBound){
        Long beginDate = lowerBound;
        if(beginDate == null){
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, PROD_COMMIT_DATE_RANGE_DEFAULT);
            beginDate = cal.getTime().getTime();
        }
        Long endDate = upperBound != null ? upperBound : new Date().getTime();

        if(beginDate <= commitTimestamp && commitTimestamp <= endDate)
        {
            return true;
        }
        return false;
    }



    //todo: make this not so ugly
    private List<PipelineResponseCommit> findNotPropagatedCommits(Map<String, PipelineCommit> startingStage, Map<String, PipelineCommit> commitsInLaterStages, Dashboard dashboard, Pipeline pipeline){
        List<PipelineResponseCommit> notPropagatedCommits = new ArrayList<>();
        for(Map.Entry entry : startingStage.entrySet()){
            if(!commitsInLaterStages.containsKey(entry.getKey())){
                PipelineResponseCommit commit = applyStageTimestamps((PipelineCommit)entry.getValue(), dashboard, pipeline);
                notPropagatedCommits.add(commit);
            }
        }
        return notPropagatedCommits;
    }

    //todo: rethink this approach.  breaking for deployment environments
    private PipelineResponseCommit applyStageTimestamps(PipelineCommit commit, Dashboard dashboard, Pipeline pipeline){
        Map<PipelineStageType, Map<String, PipelineCommit>> commitsMapByStage = getCommitsByStage(dashboard, pipeline);
        PipelineResponseCommit returnCommit = new PipelineResponseCommit(commit);
        for(PipelineStageType stageType : PipelineStageType.values()){
            Map<String, PipelineCommit> commitMap = commitsMapByStage.get(stageType);
            //if this commit doesnt have a processed timestamp for this stage, add one
            PipelineCommit pipelineCommit = commitMap.get(commit.getScmRevisionNumber());
            if(pipelineCommit != null && !returnCommit.getProcessedTimestamps().containsKey(stageType.name())){
                Long timestamp = pipelineCommit.getTimestamp();
                returnCommit.addNewPipelineProcessedTimestamp(stageType.name(), timestamp);
            }
        }
        return returnCommit;
    }

    private Map<String, PipelineCommit> commitSetToMap(Set<PipelineCommit> set){
        Map<String, PipelineCommit> returnMap = new HashMap<>();
        for(PipelineCommit commit : set){
            returnMap.put(commit.getScmRevisionNumber(), commit);
        }
        return returnMap;
    }

    //todo: make this not so ugly
    private Map<String, PipelineCommit> getCommitsAfterStage(PipelineStageType stage, Map<PipelineStageType, Map<String, PipelineCommit>> commitsByStage){
        Map<String, PipelineCommit> unionOfAllSets = new HashMap<>();
        for(PipelineStageType stageType : PipelineStageType.values()){
            if(stageType.ordinal() > stage.ordinal()){
                unionOfAllSets.putAll(commitsByStage.get(stageType));
            }
        }
        return unionOfAllSets;
    }

    //todo: make this not so ugly
    Map<PipelineStageType, Map<String, PipelineCommit>> getCommitsByStage(Dashboard dashboard, Pipeline pipeline){
        Map<PipelineStageType, Map<String, PipelineCommit>> commitsByStage = new HashMap<>();

        for(PipelineStageType pipelineStage : PipelineStageType.values()){
            EnvironmentStage stage = null;
            String environmentName;
            if(!pipelineStage.equals(PipelineStageType.Build) && !pipelineStage.equals(PipelineStageType.Commit)){
                Map<PipelineStageType, String> environmentMappings =getEnvironmentMappings(dashboard);
                environmentName = environmentMappings.get(pipelineStage);
            }
            else{
                environmentName = pipelineStage.name();
            }

            Map<String, EnvironmentStage> stages = new TreeMap<String, EnvironmentStage>(String.CASE_INSENSITIVE_ORDER);
            stages.putAll(pipeline.getStages());
            if(environmentName != null) {
                stage = stages.get(environmentName);
            }
            if(!commitsByStage.containsKey(pipelineStage)){
                commitsByStage.put(pipelineStage, new HashMap<String, PipelineCommit>());
            }
            commitsByStage.put(pipelineStage, stage != null ? commitSetToMap(stage.getCommits()) : new HashMap<String, PipelineCommit>());
        }
        return commitsByStage;
    }



    private Map<PipelineStageType, String> getEnvironmentMappings(Dashboard dashboard){
        Map<String, String> environmentMappings = null;
        for(Widget widget : dashboard.getWidgets()) {
            if (widget.getName().equalsIgnoreCase("pipeline")) {
                environmentMappings =  (Map) widget.getOptions().get("mappings");
            }
        }
        Map<PipelineStageType, String> stageTypeToEnvironmentNameMap = new HashMap<>();
        if(environmentMappings == null){
            throw new RuntimeException("No pipeline widget configured for dashboard: "+dashboard.getTitle());
        }
        else {
            for (Map.Entry mapping : environmentMappings.entrySet()) {
                stageTypeToEnvironmentNameMap.put(PipelineStageType.fromString((String) mapping.getKey()), (String) mapping.getValue());
            }
        }
        return stageTypeToEnvironmentNameMap;
    }
}
