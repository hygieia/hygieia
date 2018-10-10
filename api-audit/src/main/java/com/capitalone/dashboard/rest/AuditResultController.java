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

    @RequestMapping(value = "/auditresult/dashboard/all", method=GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResults(){
        Iterable<AuditResult> auditResults = auditResultService.getAuditResults();
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/{title}", method = GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByTitle(@Valid @PathVariable String title) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByTitle(title);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/{id}", method = GET,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditResult> getAuditResult(@Valid @PathVariable ObjectId id) {
        AuditResult auditResult = auditResultService.getAuditResult(id);
        return ResponseEntity.ok().body(auditResult);
    }
}
