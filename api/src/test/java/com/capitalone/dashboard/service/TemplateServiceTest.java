package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Template;
import com.capitalone.dashboard.repository.TemplateRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateServiceImpl templateService;


    @Test
    public void test_get() throws Exception {
        Template template = new Template("template1", getWidgetsAndOrder(), getWidgetsAndOrder());
        when(templateRepository.findByTemplate("template1")).thenReturn(template);
        Template actual = templateService.get("template1");
        assertEquals(actual, template);
    }


    @Test
    public void test_create() throws Exception {
        Template template = new Template("template1", getWidgetsAndOrder(), getWidgetsAndOrder());
        when(templateRepository.save(template)).thenReturn(template);
        Template actual = templateService.create(template);
        assertEquals(actual, template);
    }

    @Test
    public void test_update() throws Exception {
        Template template = new Template("template1", getWidgetsAndOrder(), getWidgetsAndOrder());
        when(templateRepository.save(template)).thenReturn(template);
        Template actual = templateService.update(template);
        assertEquals(actual, template);
    }

    @Test
    public void test_find() throws Exception {
        ObjectId templateId = ObjectId.get();
        Template template = new Template("template1", getWidgetsAndOrder(), getWidgetsAndOrder());
        when(templateRepository.findOne(templateId)).thenReturn(template);
        Template actual = templateService.get(templateId);
        assertEquals(actual, template);
    }


    private List<String> getWidgetsAndOrder() {
        List<String> widgets = new ArrayList<>();
        widgets.add("Build");
        widgets.add("CodeAnalysis");
        return widgets;
    }

}
