package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Template;
import org.bson.types.ObjectId;


public interface TemplateService {

    /**
     * Fetches all registered templates, sorted by title.
     *
     * @return all templates
     */
    Iterable<Template> all();


    /**
     * Fetches a template.
     */
    Template get(String template);

    /**
     * Creates a new template and saves it to the store.
     *
     * @param template
     * @return newly created template
     */
    Template create(Template template) throws HygieiaException;

    /**
     * Updates an existing template instance.
     *
     * @param template
     * @return updated
     */
    Template update(Template template) throws HygieiaException;


    /**
     * Deletes an existing template instance.
     *
     * @param ObjectId
     * @return void
     */
    void delete(ObjectId id);

    Template get(ObjectId id);
}




