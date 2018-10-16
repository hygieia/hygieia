package com.capitalone.dashboard.model.relation;

import com.capitalone.dashboard.model.BaseModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "related_items")
public class RelatedCollectorItem extends BaseModel{
    @NotNull
    private ObjectId left;
    @NotNull
    private ObjectId right;
    @NotNull
    private String source;
    @NotNull
    private String reason;
    @NotNull
    private long creationTime;

    public ObjectId getLeft() {
        return left;
    }

    public void setLeft(ObjectId left) {
        this.left = left;
    }

    public ObjectId getRight() {
        return right;
    }

    public void setRight(ObjectId right) {
        this.right = right;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
