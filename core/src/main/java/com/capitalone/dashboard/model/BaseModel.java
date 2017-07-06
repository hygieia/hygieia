package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Base class for all Mongo model classes that has an id property.
 */
public class BaseModel {
    @Id
    private ObjectId id;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    /*
     * Note:
     * 
     * Having hashcode + equals is more complicated than simply comparing ObjectIds since
     * it does not provide a way to properly compare models that have not been saved yet.
     */
}
