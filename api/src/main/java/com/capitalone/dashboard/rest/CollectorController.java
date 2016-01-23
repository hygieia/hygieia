package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveCollectorTypeEditor;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.request.CollectorItemRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.service.CollectorService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CollectorController {

    private final CollectorService collectorService;


    @Autowired
    public CollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/collector", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Collector createCollector(@Valid @RequestBody CollectorRequest request) {
        return collectorService.createCollector(request.toCollector());
    }


    @RequestMapping(value = "/collector/type/{collectorType}",
            method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Collector> collectorsByType(@PathVariable CollectorType collectorType) {
        return collectorService.collectorsByType(collectorType);
    }

    @RequestMapping(value = "/collector/item", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectorItem> createCollectorItem(@Valid @RequestBody CollectorItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectorService.createCollectorItem(request.toCollectorItem()));
    }

    @RequestMapping(value = "/collector/item/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectorItem> getCollectorItem(@PathVariable ObjectId id) {
        return ResponseEntity.ok(collectorService.getCollectorItem(id));
    }

    @RequestMapping(value = "/collector/item/type/{collectorType}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public List<CollectorItem> collectorItemsByType(@PathVariable CollectorType collectorType) {
        return collectorService.collectorItemsByType(collectorType);
    }
}
