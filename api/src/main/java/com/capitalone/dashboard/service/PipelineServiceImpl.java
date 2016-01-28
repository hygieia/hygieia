package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PipelineServiceImpl implements PipelineService {
    private final PipelineRepository pipelineRepository;

    @Autowired
    public PipelineServiceImpl(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    @Override
    public Iterable<PipelineResponse> search(PipelineSearchRequest searchRequest) {
        throw new NotImplementedException();
//        List<PipelineResponse> pipelineResponses = new ArrayList<>();

//        for (Pipeline pipeline : pipelineRepository.findByCollectorItemIdIn(searchRequest.getCollectorItemId())) {
//            PipelineResponse pipelineResponse = new PipelineResponse();
//            pipelineResponse.setCollectorItemId(pipeline.getCollectorItemId());
//            pipelineResponse.setName(pipeline.getName());
//            pipelineResponses.add(pipelineResponse);
//
//            for (PipelineCommit pipelineCommit : pipeline.getCommits()) {
//                if (pipelineCommit.getCurrentStage().equals(PipelineStageType.Prod)) {
//                    pipelineResponse.addToStage(pipelineCommit.getCurrentStage(), pipelineCommit);
//                } else if (searchRequest.hasDateRange()) {
//                    // Filter out commits in production that are outside the date range
//                    Long timestamp = pipelineCommit.getProcessedTimestamps().get(PipelineStageType.Commit);
//                    if (timestamp != null
//                            && timestamp >= searchRequest.getBeginDate()
//                            && timestamp <= searchRequest.getEndDate()) {
//                        pipelineResponse.addToStage(pipelineCommit.getCurrentStage(), pipelineCommit);
//                    }
//                }
//            }
//        }
//
//        return pipelineResponses;
    }

    private Pipeline buildPipeline(){
        //
        throw new NotImplementedException();
    }

    private PipelineStageType getPipelineStage(Dashboard dashboard, EnvironmentComponent environmentComponent){
        PipelineStageType pipelineStageType = null;
        for(Widget widget : dashboard.getWidgets()) {
            if(widget.getName().equalsIgnoreCase("pipeline")){
                Map<String, String> mappings = (Map)widget.getOptions().get("mappings");
                if(mappings == null ||mappings.size() < 1)
                {
                    return null;
                }

                for(Map.Entry entry : mappings.entrySet()){
                    if(entry.getValue().equals(environmentComponent.getEnvironmentName()))
                    {
                        pipelineStageType = PipelineStageType.fromString((String)entry.getKey());
                    }
                }
            }
        }
        return pipelineStageType;
    }

}
