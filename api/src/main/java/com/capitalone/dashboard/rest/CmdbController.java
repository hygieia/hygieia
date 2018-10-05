package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.request.CmdbRequest;
import com.capitalone.dashboard.service.CmdbRemoteService;
import com.capitalone.dashboard.service.CmdbService;
import com.capitalone.dashboard.util.PaginationHeaderUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CmdbController {
    private final CmdbService cmdbService;
    private final CmdbRemoteService cmdbRemoteService;
    private PaginationHeaderUtility paginationHeaderUtility;

    @Autowired
    public CmdbController(CmdbService cmdbService, PaginationHeaderUtility paginationHeaderUtility, CmdbRemoteService cmdbRemoteService ) {

        this.cmdbService = cmdbService;
        this.paginationHeaderUtility = paginationHeaderUtility;
        this.cmdbRemoteService = cmdbRemoteService;
    }



    @RequestMapping(value = "/cmdb/configItem/{itemType}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Cmdb>> getConfigItemByType(@PathVariable String itemType, @RequestParam(value = "search", required = false, defaultValue = "") String descriptionFilter, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {

        Page<Cmdb> pageOfConfigurationItems = cmdbService.configurationItemsByTypeWithFilter(itemType, descriptionFilter,pageable);

        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageOfConfigurationItems))
                .body(pageOfConfigurationItems.getContent());

    }

    @RequestMapping(value = "/cmdb/remoteCreate", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> remoteCreateDashboard( @Valid @RequestBody CmdbRequest request ) {
        try {
            return ResponseEntity
                    .status( HttpStatus.CREATED )
                    .body( cmdbRemoteService.remoteCreate( request ) );
        } catch (HygieiaException he) {
            return ResponseEntity
                    .status( HttpStatus.BAD_REQUEST )
                    .body( he.getMessage() );
        }
    }
}
