package com.capitalone.dashboard.rest;


import com.capitalone.dashboard.model.Issue;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.IssueRequest;
import com.capitalone.dashboard.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @RequestMapping(value = "/issues", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Issue>> search(@Valid IssueRequest request) {
        return issueService.search(request);
    }

    @RequestMapping(value = "/issuesClosed", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Iterable<Issue>> searchClosed(@Valid IssueRequest request) {
        return issueService.search(request);
    }

}
