package com.capitalone.dashboard.repository;


import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.util.GitHubParsedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public List<CollectorItem> findCollectorItemsBySubsetOptions(ObjectId id, Map<String, Object> allOptions, Map<String, Object> uniqueOptions) {
        Criteria c = Criteria.where("collectorId").is(id);
        uniqueOptions.values().removeIf(d-> d.equals(null) || ((d instanceof String) && StringUtils.isEmpty((String) d)));
        for (Map.Entry<String, Object> e : allOptions.entrySet()) {
            if (uniqueOptions.containsKey(e.getKey())) {
                c = getCriteria(uniqueOptions, c, e);
            } else {
                switch (e.getValue().getClass().getSimpleName()) {
                    case "String":
                        c = c.and("options." + e.getKey()).regex(REGEX_ANY_STRING_INCLUDING_EMPTY);
                        break;

                    case "Integer":
                        c = c.and("options." + e.getKey()).is(0);
                        break;

                    case "Long":
                        c = c.and("options." + e.getKey()).is(0);
                        break;

                    case "Double":
                        c = c.and("options." + e.getKey()).is(0.0);
                        break;

                    case "Boolean":
                        c = c.and("options." + e.getKey()).exists(true);
                        break;

                    default:
                        c = c.and("options." + e.getKey()).exists(true);
                        break;
                }
            }
        }

        List<CollectorItem> items =  template.find(new Query(c), CollectorItem.class);
        if (CollectionUtils.isEmpty(items)) {
            items = findCollectorItemsBySubsetOptionsWithNullCheck(id, allOptions, uniqueOptions);
        }
        return items;
    }

    //Due toe limitation of the query class, we have to create a second query to see if optional fields are null. This still does not handle combination of
    // initialized and null fields. Still better.
    //TODO: This needs to be re-thought out.
    private List<CollectorItem> findCollectorItemsBySubsetOptionsWithNullCheck(ObjectId id, Map<String, Object> allOptions, Map<String, Object> uniqueOptions) {
        Criteria c = Criteria.where("collectorId").is(id);
        uniqueOptions.values().removeIf(d-> d.equals(null) || ((d instanceof String) && StringUtils.isEmpty((String) d)));
        for (Map.Entry<String, Object> e : allOptions.entrySet()) {
            if (uniqueOptions.containsKey(e.getKey())) {
                c = getCriteria(uniqueOptions, c, e);
            } else {
                switch (e.getValue().getClass().getSimpleName()) {
                    case "String":
                        c = c.and("options." + e.getKey()).is(null);
                        break;

                    case "Integer":
                        c = c.and("options." + e.getKey()).is(null);
                        break;

                    case "Long":
                        c = c.and("options." + e.getKey()).is(null);
                        break;

                    case "Double":
                        c = c.and("options." + e.getKey()).is(null);
                        break;

                    case "Boolean":
                        c = c.and("options." + e.getKey()).is(null);
                        break;

                    default:
                        c = c.and("options." + e.getKey()).is(null);
                        break;
                }
            }
        }

        return template.find(new Query(c), CollectorItem.class);
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

    @Override
    public Page<CollectorItem> findByCollectorIdInAndJobNameContainingAndNiceNameContainingAllIgnoreCase(List<ObjectId> collectorIds, String jobName, String niceName, Pageable pageable) {
        Criteria c = Criteria.where("collectorId").in(collectorIds)
                .and("options.jobName").regex(Pattern.compile(jobName,Pattern.CASE_INSENSITIVE))
                .and("niceName").regex(Pattern.compile(niceName,Pattern.CASE_INSENSITIVE));
        Query query = new Query(c);
        List<CollectorItem> cItems = template.find(query, com.capitalone.dashboard.model.CollectorItem.class);
        long count = template.count(query,com.capitalone.dashboard.model.CollectorItem.class);
        Page<CollectorItem> resultPage = new PageImpl<CollectorItem>(cItems,pageable,count);
        return resultPage;
    }

    @Override
    public Page<CollectorItem> findByCollectorIdInAndJobNameContainingIgnoreCase(List<ObjectId> collectorIds, String jobName, Pageable pageable) {
        Criteria c = Criteria.where("collectorId").in(collectorIds)
                .and("options.jobName").regex(Pattern.compile(jobName,Pattern.CASE_INSENSITIVE));
        Query query = new Query(c);
        List<CollectorItem> cItems = template.find(query, com.capitalone.dashboard.model.CollectorItem.class);
        long count = template.count(query,com.capitalone.dashboard.model.CollectorItem.class);
        Page<CollectorItem> resultPage = new PageImpl<CollectorItem>(cItems,pageable,count);
        return resultPage;
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