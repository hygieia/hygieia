package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Template;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * {@link Template} repository.
 */
public interface TemplateRepository extends PagingAndSortingRepository<Template, ObjectId> {

    Template findByTemplate(String template);

}
