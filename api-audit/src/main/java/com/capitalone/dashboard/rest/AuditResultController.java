package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.service.AuditResultService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class AuditResultController {

    private final AuditResultService auditResultService;

    @Autowired
    public AuditResultController(AuditResultService auditResultService) {
        this.auditResultService = auditResultService;
    }

    @RequestMapping(value = "/auditresult/last30days", method=GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> all(){
        Iterable<AuditResult> auditResults = auditResultService.all();
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult", method = GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResults(@Valid String dashboardTitle) {
        Iterable<AuditResult> auditResults = auditResultService.findByDashboardTitle(dashboardTitle);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/{id}", method = GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditResult> getAuditResult(@Valid @PathVariable ObjectId id) {
        AuditResult auditResult = auditResultService.findById(id);
        return ResponseEntity.ok().body(auditResult);
    }
}
