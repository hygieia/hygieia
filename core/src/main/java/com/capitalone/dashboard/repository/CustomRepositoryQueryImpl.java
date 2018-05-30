package com.capitalone.dashboard.repository;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CustomRepositoryQueryImpl implements CustomRepositoryQuery {

    private final MongoTemplate template;
    private static final String REGEX_ANY_STRING_INCLUDING_EMPTY = "^$|^.*";

    @Autowired
    public CustomRepositoryQueryImpl(MongoTemplate template) {
        this.template = template;
    }


    @Override
    public List<CollectorItem> findCollectorItemsBySubsetOptions(ObjectId id, Map<String, Object> allOptions, Map<String, Object> uniqueOptions,Map<String, Object> uniqueOptionsFromCollector) {
        Criteria c = Criteria.where("collectorId").is(id);
        uniqueOptions.values().removeIf(d-> d.equals(null) || ((d instanceof String) && StringUtils.isEmpty((String) d)));
        for (Map.Entry<String, Object> e : allOptions.entrySet()) {
            if (uniqueOptionsFromCollector.containsKey(e.getKey())) {
                c = getCriteria(uniqueOptions, c, e);
            }
        }
        List<CollectorItem> items =  template.find(new Query(c), CollectorItem.class);
        return items;
    }

    @Override
    public List<com.capitalone.dashboard.model.Component> findComponents(Collector collector) {
        Criteria c = Criteria.where("collectorItems." + collector.getCollectorType() + ".collectorId").is(collector.getId());
        return template.find(new Query(c), com.capitalone.dashboard.model.Component.class);
    }

    @Override
    public List<com.capitalone.dashboard.model.Component> findComponents(CollectorType collectorType) {
        Criteria c = Criteria.where("collectorItems." + collectorType).exists(true);
        return template.find(new Query(c), com.capitalone.dashboard.model.Component.class);
    }


    @Override
    public List<com.capitalone.dashboard.model.Component> findComponents(Collector collector, CollectorItem collectorItem) {
        return findComponents(collector.getId(), collector.getCollectorType(), collectorItem.getId());
    }

    @Override
    public List<com.capitalone.dashboard.model.Component> findComponents(ObjectId collectorId, CollectorType collectorType, CollectorItem collectorItem) {
        return findComponents(collectorId, collectorType, collectorItem.getId());
    }

    @Override
    public List<com.capitalone.dashboard.model.Component> findComponents(ObjectId collectorId, CollectorType collectorType, ObjectId collectorItemId) {
        Criteria c = Criteria.where("collectorItems." + collectorType + "._id").is(collectorItemId);
        return template.find(new Query(c), com.capitalone.dashboard.model.Component.class);
    }

	private String getGitHubParsedString(Map<String, Object> options, Map.Entry<String, Object> e) {
        String url = (String)options.get(e.getKey());
        GitHubParsedUrl gitHubParsedUrl = new GitHubParsedUrl(url);
        return gitHubParsedUrl.getUrl();
    }

    private Criteria getCriteria(Map<String, Object> options, Criteria c, Map.Entry<String, Object> e) {
        Criteria criteria = c;
        if("url".equalsIgnoreCase(e.getKey())){
            String url = getGitHubParsedString(options, e);
            criteria = criteria.and("options." + e.getKey()).regex(Pattern.compile(url,Pattern.CASE_INSENSITIVE));
        }
        else if("branch".equalsIgnoreCase(e.getKey())){
            String branch = (String)options.get(e.getKey());
            criteria = criteria.and("options." + e.getKey()).regex(Pattern.compile(branch,Pattern.CASE_INSENSITIVE));
        }
        else {
            criteria = criteria.and("options." + e.getKey()).is(options.get(e.getKey()));
        }
        return criteria;
    }

}