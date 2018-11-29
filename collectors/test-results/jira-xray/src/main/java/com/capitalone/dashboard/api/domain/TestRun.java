package com.capitalone.dashboard.api.domain;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class will get the details from a Test Run
 */
public class TestRun extends BasicIssue implements Versionable<TestRun> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRun.class);
    private TestRun oldVersion;
    private int version=0;

    private Status status;
    private String executedBy;
    private String assignee;
    private Date startedOn;
    private Date finishedOn;
    private Iterable<Defect> defects;
    private Iterable<Evidence> evidences;
    private Comment comment;

    private Iterable<TestStep> steps;
    private Iterable<Example> examples;

    //TODO: Need to be implemented scenario &

    private String testExecKey;

    public TestRun(URI self,String key,Long id){
        super(self,key,id);
    }

    public TestRun(URI self, String key, Long id, Status status, Date startedOn, Date finishedOn, String assignee, String executedBy, Iterable<TestStep>steps) {
        super(self, key, id);
        this.status=status;
        this.assignee=assignee;
        this.executedBy=executedBy;
        this.startedOn=startedOn;
        this.finishedOn=finishedOn;
        this.steps=steps;
        try {
            this.oldVersion= cloneTestRun();
        } catch (CloneNotSupportedException e) {
            LOGGER.error("Clone is not supported for the TestRun: " + e);
        }
    }

    // TODO: ADD CLONE TO ARRAYS USED IN THE TEST RUN
    public final TestRun cloneTestRun() throws CloneNotSupportedException {
        TestRun myTestRun= null;
            myTestRun = new TestRun(super.getSelf(),super.getKey(),super.getId());
            if(this.executedBy!=null)
                myTestRun.setExecutedBy(this.executedBy);
            if(this.comment!=null){
                myTestRun.setComment(this.comment.cloneComment());
            }
            if(this.status!=null){
            myTestRun.setStatus(this.status);
            }
            if(this.assignee!=null)
                myTestRun.setAssignee(this.assignee);
            if(this.startedOn!=null)
                myTestRun.setStartedOn((Date) this.startedOn.clone());
            if(this.finishedOn!=null)
                myTestRun.setFinishedOn((Date) this.finishedOn.clone());
            if(this.defects!=null){
                List<Defect> cloneDefects=new ArrayList<Defect>();
                for(Defect def:this.defects){
                    cloneDefects.add(def.cloneDefect());
                }
                myTestRun.setDefects(cloneDefects);
            }
        return myTestRun;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        try {
            this.setOldVersion(this.cloneTestRun());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CAN'T CLONE MYSELF SO VERSIONABLE OBJECT IS LOST");
        }
        this.status = status;
    }

    public Iterable<Defect> getDefects() {
        return defects;
    }

    public void setDefects(Iterable<Defect> defects) {
        try {
            this.setOldVersion(this.cloneTestRun());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CAN'T CLONE MYSELF SO VERSIONABLE OBJECT IS LOST");
        }
        this.defects = defects;
    }

    public Iterable<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        try {
            this.setOldVersion(this.cloneTestRun());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CAN'T CLONE MYSELF SO VERSIONABLE OBJECT IS LOST");
        }
        this.comment = comment;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getFinishedOn() {
        return finishedOn;
    }

    public void setFinishedOn(Date finishedOn){this.finishedOn=finishedOn;}

    public Iterable<TestStep> getSteps() {
        return steps;
    }

    public void setSteps(Iterable<TestStep> steps) {
        try {
            this.setOldVersion(this.cloneTestRun());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CAN'T CLONE MYSELF SO VERSIONABLE OBJECT IS LOST");
        }
        this.steps = steps;
    }

    public Iterable<Example> getExamples() {
        return examples;
    }

    public void setExamples(Iterable<Example> examples) {
        try {
            this.setOldVersion(this.cloneTestRun());
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("CAN'T CLONE MYSELF SO VERSIONABLE OBJECT IS LOST");
        }
        this.examples = examples;
    }

    public TestRun getOldVersion() {
        return this.oldVersion;
    }

    public void setOldVersion(TestRun oldVersion) {
        if(this.oldVersion==null){
            this.oldVersion=oldVersion;
        }
        this.version=1;
    }

    public int getVersion() {
        return version;
    }

    protected void resetVersion(){
        this.version=0;
        this.oldVersion=null;
    }

    public String getTestExecKey() {
        return testExecKey;
    }

    public void setTestExecKey(String testExecKey) {
        this.testExecKey = testExecKey;
    }


    public enum Status{TODO,EXECUTING,ABORTED,FAIL,PASS}


}
