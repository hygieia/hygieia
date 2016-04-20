package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;

/**
 * Base class for all Mongo model classes that has an id property.
 */
@Data
public class BaseModel {
    @Id
    private ObjectId id;
}
