package com.capitalone.dashboard.model.pullrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "pull_requests")
@CompoundIndexes({
        @CompoundIndex(name = "unique_scm_key", def = "{'collectorItemId': 1, 'createdDate': -1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements Serializable {
    private static final long serialVersionUID = 5802175698076010621L;
    @Id
    private long id;
    @Indexed
    private ObjectId collectorItemId;
    private int timestamp;

    private String title;
    private String state;
    private boolean open;
    private boolean closed;
    private long createdDate;
    private long updatedDate;

    private PullRequestRef fromRef;

    private PullRequestRef toRef;

    private PullRequestUser author;

    private PullRequestProperties properties;

    public PullRequest() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public PullRequestUser getAuthor() {
        return author;
    }

    public PullRequestRef getFromRef() {
        return fromRef;
    }

    public void setFromRef(PullRequestRef fromRef) {
        this.fromRef = fromRef;
    }

    public PullRequestRef getToRef() {
        return toRef;
    }

    public void setToRef(PullRequestRef toRef) {
        this.toRef = toRef;
    }

    public void setAuthor(PullRequestUser author) {
        this.author = author;
    }

    public PullRequestProperties getProperties() {
        return properties;
    }

    public void setProperties(PullRequestProperties properties) {
        this.properties = properties;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }
}
