package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="test_results")
public class ReportResult extends TestResult {

	private String name;
	//private String Id;
    private ObjectId collectorId;
    
    
	//private ObjectId collectorItemId;
    //private long lastUpdated;
    private String launchId;
    //private String lastExecuted;
    private Map<String, Object> results=new HashMap<>();;
    
    @Indexed
    private ObjectId testId;
    
    
  
    public Map<String, Object> getResults() {
        return results;
    }
    public void setResults(Map<String,Object> results) {
        this.results = results;
    }
    public void setName(String name) {
    	this.name=name;
    }
    
    public ObjectId getCollectorItemId() {
    	return this.collectorId;
    }
    
    public  void setCollectorItemId(ObjectId collectorItemId ) {
    	 super.setCollectorItemId(collectorItemId);
    }
   public void setTestId( ObjectId testId) {
	   this.testId=testId;
   }
   
   public ObjectId getTestId() {
	   return this.testId;
   }
   
    
    public String getName() {
    	return this.name;
    }
    
    public void setCollectorId(ObjectId collectorId) {
    	this.collectorId=collectorId;
    }
    
    public ObjectId getCollectorId() {
    	return this.collectorId;
    }
    
    public String getLaunchId() {
    	return this.launchId;
    }
    
    public void setLaunchId(String launchId) {
    	this.launchId=launchId;
    }

}
