package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.service.AuditResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class AuditResultController {

    private final AuditResultService auditResultService;

    @Autowired
    public AuditResultController(AuditResultService auditResultService) {
        this.auditResultService = auditResultService;
    }

    @RequestMapping(value = "/auditresult/all", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> all(){
        Iterable<AuditResult> auditResults = auditResultService.all();
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/{dashboardTitle}", method = GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditResult> findByDashboardTitle(@PathVariable String dashboardTitle) {
        AuditResult auditResult = auditResultService.findByDashboardTitle(dashboardTitle);
        return ResponseEntity.ok().body(auditResult);
    }
}
