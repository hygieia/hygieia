package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="test_results")
public class ReportResult extends TestResult {

	private String name;
	//private String Id;
    private ObjectId collectorId;
    //private long lastUpdated;
    private String launchId;
    @Indexed
    private String testId;
    
    public void setName(String name) {
    	this.name=name;
    }
    
   public void setTestId(String testId) {
	   this.testId=testId;
   }
   
   public String getTestId() {
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
