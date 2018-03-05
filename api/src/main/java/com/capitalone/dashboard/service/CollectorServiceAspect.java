package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class CollectorServiceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorServiceAspect.class);

    private final CollectorRepository collectorRepository;

    @Autowired
    public CollectorServiceAspect(CollectorRepository collectorRepository) {
        this.collectorRepository = collectorRepository;
    }

    private boolean isGit(Collector collector) {
        String name = collector.getName().toUpperCase();
        boolean isGit = true;
        switch (name) {
            case "GITHUB":
                isGit = true;
                break;
            case "SUBVERSION":
                isGit = false;
                break;
            default:
                isGit = true;
                break;
        }
        return isGit;
    }

    private void normalizeOptions(CollectorItem item, Map<String, Object> uniqueOptions) {
        Collector collector = collectorRepository.findOne(item.getCollectorId());
        if (collector.getCollectorType() == CollectorType.SCM && isGit(collector)) {
            String repoUrl = (String)uniqueOptions.get("url");
            GitHubParsedUrl gitHubParsed = new GitHubParsedUrl(repoUrl);
            uniqueOptions.put("url", gitHubParsed.getUrl());
        }
    }

    @Before("execution(* com.capitalone.dashboard.service.CollectorService.createCollectorItemSelectOptions (com.capitalone.dashboard.model.CollectorItem, java.util.Map<String, Object>, java.util.Map<String, Object>)) && args(item, allOptions, uniqueOptions)")
    public void normalizeCreateItem(CollectorItem item, Map<String, Object> allOptions, Map<String, Object> uniqueOptions) {
        LOGGER.debug("normalizeCreateItem " + item.getNiceName());
        normalizeOptions(item, uniqueOptions);
    }
}
