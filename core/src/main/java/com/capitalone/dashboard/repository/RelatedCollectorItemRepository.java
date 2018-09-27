package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.relation.RelatedCollectorItem;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Build} data.
 */
public interface RelatedCollectorItemRepository extends CrudRepository<RelatedCollectorItem, ObjectId>, QueryDslPredicateExecutor<RelatedCollectorItem> {
    List<RelatedCollectorItem> findRelatedCollectorItemByLeft(ObjectId left);
    List<RelatedCollectorItem> findRelatedCollectorItemByRight(ObjectId right);
    RelatedCollectorItem findByLeftAndRight(ObjectId left, ObjectId right);

    default RelatedCollectorItem saveRelatedItems(ObjectId left, ObjectId right, String source, String reason) {
        RelatedCollectorItem related = new RelatedCollectorItem();
        related.setLeft(left);
        related.setRight(right);
        related.setCreationTime(System.currentTimeMillis());
        related.setSource(source);
        related.setReason(reason);
        RelatedCollectorItem item = findByLeftAndRight(left, right);
        if (findByLeftAndRight(left, right) == null) {
            return save(related);
        }
        return item;
    }
}
