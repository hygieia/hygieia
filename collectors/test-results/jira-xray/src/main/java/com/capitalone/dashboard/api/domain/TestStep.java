package com.capitalone.dashboard.api.domain;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.capitalone.dashboard.core.json.util.RendereableItem;

import java.net.URI;

/**
 * This class will get the details from a Test Step
 */
public class TestStep extends BasicIssue implements Versionable<TestStep> {

    private int version = 0;
    private TestStep oldVersion = null;

    //TODO: To be used in future
    private Integer index;
    private RendereableItem step;
    private RendereableItem data;
    private RendereableItem result;
    private Iterable<Evidence> attachments;
    private Iterable<Evidence> evidences;
    Iterable<Defect> defects;
    private Comment comment;
    private Status status;


    public TestStep(URI self, String key, Long id) {
        super(self, key, id);
    }

    public TestStep(URI self, String key, Long id, Integer index, RendereableItem step, RendereableItem data, RendereableItem result, Status status) {
        super(self, key, id);
        this.index = index;
        this.step = step;
        this.data = data;
        this.result = result;
        this.status = status;
    }


    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public RendereableItem getStep() {
        return step;
    }

    public void setStep(RendereableItem step) {
        this.step = step;
    }

    public RendereableItem getData() {
        return data;
    }

    public void setData(RendereableItem data) {
        this.data = data;
    }

    public RendereableItem getResult() {
        return result;
    }

    public void setResult(RendereableItem result) {
        this.result = result;
    }

    public Iterable<Evidence> getAttachments() {
        return attachments;
    }

    public void setAttachments(Iterable<Evidence> attachments) {
        this.setOldVersion(this);
        this.attachments = attachments;
    }

    public TestStep getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(TestStep oldVersion) {
        if (this.oldVersion == null)
            this.oldVersion = oldVersion;
        this.version = 1;
    }

    public int getVersion() {
        return version;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Iterable<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(Iterable<Evidence> evidences) {
        this.evidences = evidences;
    }

    public Iterable<Defect> getDefects() {
        return defects;
    }

    public enum Status {TODO, EXECUTING, ABORTED, FAIL, PASS, SKIP}

    public Status getStatus() {
        return status;
    }

}


