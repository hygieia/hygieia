package com.capitalone.dashboard.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@RunWith(MockitoJUnitRunner.class)
public class SplunkLoggingConditionTest {

    @Mock
    private ConditionContext context;
    
    @Mock
    private Environment environment;
    
    @Mock 
    private AnnotatedTypeMetadata metadata;
    
    @InjectMocks
    private SplunkLoggingCondition condition;
    
    @Test
    public void shouldBeTrueWhenPropertyTrue() {
        SplunkLoggingCondition condition = new SplunkLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(SplunkLoggingCondition.LOG_SPLUNK_REQUEST)).thenReturn("true");
        
        assertTrue(condition.matches(context, metadata));
    }
    
    @Test
    public void shouldBeFalseWhenPropertyFalse() {
        SplunkLoggingCondition condition = new SplunkLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(SplunkLoggingCondition.LOG_SPLUNK_REQUEST)).thenReturn("false");
        
        assertFalse(condition.matches(context, metadata));
    }
    
    @Test
    public void shouldBeFalseWhenPropertyNull() {
        SplunkLoggingCondition condition = new SplunkLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(SplunkLoggingCondition.LOG_SPLUNK_REQUEST)).thenReturn(null);
        
        assertFalse(condition.matches(context, metadata));
    }

}
