package com.capitalone.dashboard.logging;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DatabaseLoggingCondition implements Condition {

    protected static final String LOG_REQUEST = "logRequest";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String logRequest = context.getEnvironment().getProperty(LOG_REQUEST);
        return logRequest != null && Boolean.parseBoolean(logRequest);
    }

}
