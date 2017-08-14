package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomRepositoryQueryImpl implements CustomRepositoryQuery {

    private final MongoTemplate template;
    private static final String REGEX_ANY_STRING_INCLUDING_EMPTY = "^$|^.*";

    @Autowired
    public CustomRepositoryQueryImpl(MongoTemplate template) {
        this.template = template;
    }


    @Override
    public List<CollectorItem> findCollectorItemsBySubsetOptions(ObjectId id, Map<String, Object> allOptions, Map<String, Object> selectOptions) {
        Criteria c = Criteria.where("collectorId").is(id);

        for (Map.Entry<String, Object> e : allOptions.entrySet()) {
            if (selectOptions.containsKey(e.getKey())) {
                c = c.and("options." + e.getKey()).is(selectOptions.get(e.getKey()));
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
            items = findCollectorItemsBySubsetOptionsWithNullCheck(id, allOptions, selectOptions);
        }
        return items;
    }

    //Due toe limitation of the query class, we have to create a second query to see if optional fields are null. This still does not handle combination of
    // initialized and null fields. Still better.
    //TODO: This needs to be re-thought out.
    public List<CollectorItem> findCollectorItemsBySubsetOptionsWithNullCheck(ObjectId id, Map<String, Object> allOptions, Map<String, Object> selectOptions) {
        Criteria c = Criteria.where("collectorId").is(id);

        for (Map.Entry<String, Object> e : allOptions.entrySet()) {
            if (selectOptions.containsKey(e.getKey())) {
                c = c.and("options." + e.getKey()).is(selectOptions.get(e.getKey()));
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
}