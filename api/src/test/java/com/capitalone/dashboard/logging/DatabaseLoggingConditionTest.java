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
public class DatabaseLoggingConditionTest {
    
    @Mock
    private ConditionContext context;
    
    @Mock
    private Environment environment;
    
    @Mock 
    private AnnotatedTypeMetadata metadata;
    
    @InjectMocks
    private DatabaseLoggingCondition condition;
    
    @Test
    public void shouldBeTrueWhenPropertyTrue() {
        DatabaseLoggingCondition condition = new DatabaseLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(DatabaseLoggingCondition.LOG_REQUEST)).thenReturn("true");
        
        assertTrue(condition.matches(context, metadata));
    }
    
    @Test
    public void shouldBeFalseWhenPropertyFalse() {
        DatabaseLoggingCondition condition = new DatabaseLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(DatabaseLoggingCondition.LOG_REQUEST)).thenReturn("false");
        
        assertFalse(condition.matches(context, metadata));
    }
    
    @Test
    public void shouldBeFalseWhenPropertyNull() {
        DatabaseLoggingCondition condition = new DatabaseLoggingCondition();
        
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getProperty(DatabaseLoggingCondition.LOG_REQUEST)).thenReturn(null);
        
        assertFalse(condition.matches(context, metadata));
    }

}
