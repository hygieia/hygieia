package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Pipeline;
import com.capitalone.dashboard.model.PipelineCommit;
import com.capitalone.dashboard.model.PipelineResponse;
import com.capitalone.dashboard.model.PipelineStageType;
import com.capitalone.dashboard.repository.PipelineRepository;
import com.capitalone.dashboard.request.PipelineSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineServiceImpl implements PipelineService {
    private final PipelineRepository pipelineRepository;

    @Autowired
    public PipelineServiceImpl(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    @Override
    public Iterable<PipelineResponse> search(PipelineSearchRequest searchRequest) {
        List<PipelineResponse> pipelineResponses = new ArrayList<>();

        for (Pipeline pipeline : pipelineRepository.findByCollectorItemIdIn(searchRequest.getCollectorItemId())) {
            PipelineResponse pipelineResponse = new PipelineResponse();
            pipelineResponse.setCollectorItemId(pipeline.getCollectorItemId());
            pipelineResponse.setName(pipeline.getName());
            pipelineResponses.add(pipelineResponse);

            for (PipelineCommit pipelineCommit : pipeline.getCommits()) {
                if (pipelineCommit.getCurrentStage().equals(PipelineStageType.Prod)) {
                    pipelineResponse.addToStage(pipelineCommit.getCurrentStage(), pipelineCommit);
                } else if (searchRequest.hasDateRange()) {
                    // Filter out commits in production that are outside the date range
                    Long timestamp = pipelineCommit.getProcessedTimestamps().get(PipelineStageType.Commit);
                    if (timestamp != null
                            && timestamp >= searchRequest.getBeginDate()
                            && timestamp <= searchRequest.getEndDate()) {
                        pipelineResponse.addToStage(pipelineCommit.getCurrentStage(), pipelineCommit);
                    }
                }
            }
        }

        return pipelineResponses;
    }
}
