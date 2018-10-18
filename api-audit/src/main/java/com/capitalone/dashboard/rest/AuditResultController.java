package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.AuditResult;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.service.AuditResultService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class AuditResultController {

    private final AuditResultService auditResultService;

    @Autowired
    public AuditResultController(AuditResultService auditResultService) {
        this.auditResultService = auditResultService;
    }

    @RequestMapping(value = "/auditresult/dashboard/all/pages", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsAll(Pageable pageable) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsAll(pageable);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/audittype/{auditType}/pages", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByAuditType(@Valid @PathVariable AuditType auditType, Pageable pageable) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByAuditType(auditType, pageable);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/title/{title}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByDBoardTitle(@Valid @PathVariable String title) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByDBoardTitle(title);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/title/{title}/audittype/{auditType}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByDBoardTitleAndAuditType(@Valid @PathVariable String title,
                                                                                          @Valid @PathVariable AuditType auditType) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByDBoardTitleAndAuditType(title, auditType);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/product/{configItemBusServName}/component/{configItemBusAppName}",
            method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByServAndAppNames(@Valid @PathVariable String configItemBusServName,
                                                                                  @Valid @PathVariable String configItemBusAppName) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByServAndAppNames(configItemBusServName,
                configItemBusAppName);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/dashboard/product/{configItemBusServName}/component/{configItemBusAppName}/audittype/{auditType}",
            method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByServAndAppNamesAndAuditType(@Valid @PathVariable String configItemBusServName,
                                                                                              @Valid @PathVariable String configItemBusAppName,
                                                                                              @Valid @PathVariable AuditType auditType) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByServAndAppNamesAndAuditType(configItemBusServName,
                configItemBusAppName, auditType);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/lob/{lineOfBusiness}/audittype/{auditType}/pages", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByLineOfBusAndAuditType(@Valid @PathVariable String lineOfBusiness,
                                                                                        @Valid @PathVariable AuditType auditType, Pageable pageable) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByLineOfBusAndAuditType(lineOfBusiness, auditType, pageable);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/lob/{lineOfBusiness}/pages", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<AuditResult>> getAuditResultsByLineOfBus(@Valid @PathVariable String lineOfBusiness, Pageable pageable) {
        Iterable<AuditResult> auditResults = auditResultService.getAuditResultsByLineOfBus(lineOfBusiness, pageable);
        return ResponseEntity.ok().body(auditResults);
    }

    @RequestMapping(value = "/auditresult/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditResult> getAuditResult(@Valid @PathVariable ObjectId id) {
        AuditResult auditResult = auditResultService.getAuditResult(id);
        return ResponseEntity.ok().body(auditResult);
    }
}
