package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.TestResultCollector;
import org.springframework.stereotype.Component;

/**
 * Repository for {@link TestResultCollector}.
 */
@Component
public interface TestResultCollectorRepository extends
        BaseCollectorRepository<TestResultCollector> {
}