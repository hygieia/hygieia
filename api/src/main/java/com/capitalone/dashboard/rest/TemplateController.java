package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Template;
import com.capitalone.dashboard.request.CreateTemplateRequest;
import com.capitalone.dashboard.service.TemplateService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class TemplateController {

    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @RequestMapping(value = "/templates", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Template> templates() {
        return templateService.all();
    }

    @RequestMapping(value = "/template", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(templateService.create(request.toTemplate()));
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @RequestMapping(value = "/template/{template}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Template getTemplate(@PathVariable String template) {
        return templateService.get(template);
    }

    @RequestMapping(value = "/template/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTemplate(@PathVariable ObjectId id,
                                                 @RequestBody CreateTemplateRequest request) {
        try {
            templateService.update(request.copyTo(templateService.get(id)));
            return ResponseEntity.ok("Updated");
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @RequestMapping(value = "/template/{id}", method = DELETE)
    public ResponseEntity<Void> deleteTemplate(@PathVariable ObjectId id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
