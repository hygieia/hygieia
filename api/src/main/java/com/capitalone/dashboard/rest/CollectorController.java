package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.editors.CaseInsensitiveCollectorTypeEditor;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.request.CollectorItemRequest;
import com.capitalone.dashboard.service.CollectorService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CollectorController {

    private final CollectorService collectorService;

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    public CollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CollectorType.class, new CaseInsensitiveCollectorTypeEditor());
    }

    @RequestMapping(value = "/collector/type/{collectorType}", method = GET, produces = JSON)
    public List<Collector> collectorsByType(@PathVariable CollectorType collectorType) {
        return collectorService.collectorsByType(collectorType);
    }

    @RequestMapping(value = "/collector/item", method = POST, consumes = JSON, produces = JSON)
    public ResponseEntity<CollectorItem> createCollectorItem(@Valid @RequestBody CollectorItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectorService.createCollectorItem(request.toCollectorItem()));
    }

    @RequestMapping(value = "/collector/item/{id}", method = GET, produces = JSON)
    public ResponseEntity<CollectorItem> getCollectorItem(@PathVariable ObjectId id) {
        return ResponseEntity.ok(collectorService.getCollectorItem(id));
    }

    @RequestMapping(value = "/collector/item/type/{collectorType}", method = GET, produces = JSON)
    public List<CollectorItem> collectorItemsByType(@PathVariable CollectorType collectorType) {
        return collectorService.collectorItemsByType(collectorType);
    }
}
