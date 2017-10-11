package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Template;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TemplateRepositoryTest extends FongoBaseRepositoryTest {

    private static Template mockTemplate;

    @Autowired
    private TemplateRepository templateRepository;

    @Before
    public void setUp() {
        mockTemplate = new Template("template1", getWidgetsAndOrder(), getWidgetsAndOrder());
    }

    @After
    public void tearDown() {
        mockTemplate = null;
        templateRepository.deleteAll();
    }

    @Test
    public void validate_save() {
        templateRepository.save(mockTemplate);
        assertTrue(
                "Happy-path MongoDB connectivity validation for the FeatureRepository has failed",
                templateRepository.findAll().iterator().hasNext());
    }

    @Test
    public void validate_get() {
        templateRepository.save(mockTemplate);
        Template actual = templateRepository.findByTemplate("template1");
        assertEquals(actual.getTemplate(), mockTemplate.getTemplate());
    }

    @Test
    public void validate_delete() {
        ObjectId templateId = ObjectId.get();
        mockTemplate.setId(templateId);
        templateRepository.save(mockTemplate);
        templateRepository.delete(templateId);
        Template actual = templateRepository.findByTemplate("template1");
        assertNull(actual);
    }


    private List<String> getWidgetsAndOrder() {
        List<String> widgets = new ArrayList<>();
        widgets.add("Build");
        widgets.add("CodeAnalysis");
        return widgets;
    }
}