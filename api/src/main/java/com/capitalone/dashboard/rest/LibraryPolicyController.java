package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveCodeQualityTypeEditor;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.request.LibraryPolicyRequest;
import com.capitalone.dashboard.service.LibraryPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class LibraryPolicyController {

    private final LibraryPolicyService libraryPolicyService;

    @Autowired
    public LibraryPolicyController(LibraryPolicyService libraryPolicyService) {
        this.libraryPolicyService = libraryPolicyService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CodeQualityType.class, new CaseInsensitiveCodeQualityTypeEditor());
    }

    @RequestMapping(value = "/libraryPolicy", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<LibraryPolicyResult>> libraryPolicySearch(@Valid LibraryPolicyRequest request) {
        return libraryPolicyService.search(request);
    }
}
