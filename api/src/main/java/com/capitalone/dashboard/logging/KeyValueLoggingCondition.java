package com.capitalone.dashboard.logging;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class KeyValueLoggingCondition implements Condition {

    protected static final String LOG_REQUEST_KEY_VALUE = "logRequestKeyValue";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String logRequestKeyValue = context.getEnvironment().getProperty(LOG_REQUEST_KEY_VALUE);
        return logRequestKeyValue != null && Boolean.parseBoolean(logRequestKeyValue);
    }

}
