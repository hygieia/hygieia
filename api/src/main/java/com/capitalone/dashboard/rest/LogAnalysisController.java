package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.request.LogAnalysisSearchRequest;
import com.capitalone.dashboard.service.LogAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LogAnalysisController {

    private final LogAnalysisService logAnalysisService;

    @Autowired
    public LogAnalysisController(LogAnalysisService logAnalysisService) {
        this.logAnalysisService = logAnalysisService;
    }

    @RequestMapping(value="/loganalysis", method = RequestMethod.GET)
    public DataResponse<Iterable<LogAnalysis>> findAllLogAnalysisJobs(@Valid LogAnalysisSearchRequest request){
        return this.logAnalysisService.search(request);
    }
}
