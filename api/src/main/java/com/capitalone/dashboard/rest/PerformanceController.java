package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveCodeQualityTypeEditor;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Performance;
import com.capitalone.dashboard.model.PerformanceType;
import com.capitalone.dashboard.request.PerformanceCreateRequest;
import com.capitalone.dashboard.request.PerformanceSearchRequest;
import com.capitalone.dashboard.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class PerformanceController {

    private final PerformanceService performanceService;

    @Autowired
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CodeQualityType.class, new CaseInsensitiveCodeQualityTypeEditor());
    }

    @RequestMapping(value = "/performance", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Performance>> performanceData (@Valid PerformanceSearchRequest request) {
        return performanceService.search(request);
    }

    @RequestMapping(value = "/performance/application", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Performance>> searchApplicationPerformance(@Valid PerformanceSearchRequest request) {
        request.setType(PerformanceType.ApplicationPerformance);
        return performanceService.search(request);
    }

    @RequestMapping(value = "/performance/infrastructure", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Performance>> searchAInfrastructurePerformance(@Valid PerformanceSearchRequest request) {
        request.setType(PerformanceType.InfrastructurePerformance);
        return performanceService.search(request);
    }

    @RequestMapping(value = "/performance/create", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createPerformance(@Valid @RequestBody PerformanceCreateRequest request) throws HygieiaException {
        String response = performanceService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}
