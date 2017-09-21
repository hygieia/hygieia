package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Template;
import com.capitalone.dashboard.repository.TemplateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;


    @Autowired
    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Iterable<Template> all() {
        return templateRepository.findAll();
    }

    @Override
    public Template get(String template) {
        return templateRepository.findByTemplate(template);
    }

    @Override
    public Template create(Template template) throws HygieiaException {
        return templateRepository.save(template);
    }

    @Override
    public Template update(Template template) throws HygieiaException {
        return templateRepository.save(template);
    }

    @Override
    public void delete(ObjectId id) {
        templateRepository.delete(id);
    }


    @Override
    public Template get(ObjectId id) {
        Template template = templateRepository.findOne(id);
        return template;
    }

}
