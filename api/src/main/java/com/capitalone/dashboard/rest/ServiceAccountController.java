package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.ServiceAccount;
import com.capitalone.dashboard.request.ServiceAccountRequest;
import com.capitalone.dashboard.service.ServiceAccountService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/serviceaccount_deprecated")
@Admin
public class ServiceAccountController {

    private final ServiceAccountService serviceAccountService;

    @Autowired
    public ServiceAccountController(ServiceAccountService serviceAccountService) {
        this.serviceAccountService = serviceAccountService;
    }
    


    @RequestMapping(value = "/createAccount", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAccount(@Valid @RequestBody ServiceAccountRequest serviceAccountRequest) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(serviceAccountService.createAccount(serviceAccountRequest.getServiceAccount(),
                            serviceAccountRequest.getFileNames()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @RequestMapping(value = "/updateAccount/{id}", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAccount(@Valid @RequestBody ServiceAccountRequest serviceAccountRequest, @PathVariable ObjectId id) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(serviceAccountService.updateAccount(serviceAccountRequest.getServiceAccount(),
                            serviceAccountRequest.getFileNames(),id));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @RequestMapping(path = "/allServiceAccounts", method = RequestMethod.GET)
    public Collection<ServiceAccount> getAllServiceAccouts() {
        Collection<ServiceAccount> serviceAccounts = serviceAccountService.getAllServiceAccounts();
        return serviceAccounts;
    }


    @RequestMapping(path = "/deleteAccount/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAccount(@PathVariable ObjectId id){
         serviceAccountService.deleteAccount(id);
         return ResponseEntity.<Void>noContent().build();
    }
}
