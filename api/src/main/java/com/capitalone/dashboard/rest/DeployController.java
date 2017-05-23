package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.deploy.Environment;
import com.capitalone.dashboard.request.DeployDataCreateRequest;
import com.capitalone.dashboard.service.DeployService;

@RestController
public class DeployController {


    private final DeployService deployService;

    @Autowired
    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @RequestMapping(value = "/deploy/status/{componentId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable ObjectId componentId) {
        return deployService.getDeployStatus(componentId);
    }

    @RequestMapping(value = "/deploy/status/application/{applicationName}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<List<Environment>> deployStatus(@PathVariable String applicationName) {
        return deployService.getDeployStatus(applicationName);
    }

    @RequestMapping(value = "/deploy", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBuild(@Valid @RequestBody DeployDataCreateRequest request) throws HygieiaException {
        String response = deployService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @RequestMapping(value = "/deploy/rundeck", method = POST,
            consumes = TEXT_XML_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRundeckBuild(HttpServletRequest request,
            @RequestHeader("X-Rundeck-Notification-Execution-ID") String executionId, 
            @RequestHeader("X-Rundeck-Notification-Trigger") String status) throws HygieiaException{
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(request.getInputStream()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new HygieiaException(e);
        }        
        String response = deployService.createRundeckBuild(doc, request.getParameterMap(), executionId, status);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);        
    }
}
