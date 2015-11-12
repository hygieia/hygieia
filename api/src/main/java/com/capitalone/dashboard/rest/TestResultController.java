package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveTestSuiteTypeEditor;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.request.TestResultRequest;
import com.capitalone.dashboard.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class TestResultController {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final TestResultService testResultService;

    @Autowired
    public TestResultController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CodeQualityType.class, new CaseInsensitiveTestSuiteTypeEditor());
    }

    @RequestMapping(value = "/quality/test", method = GET, produces = JSON)
    public DataResponse<Iterable<TestResult>> qualityData(@Valid TestResultRequest request) {
        return testResultService.search(request);
    }
}
