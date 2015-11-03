package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Component;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link Component} repository.
 */
public interface ComponentRepository extends CrudRepository<Component, ObjectId> {
}
