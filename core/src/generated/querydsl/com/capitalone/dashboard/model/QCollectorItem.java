package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCollectorItem is a Querydsl query type for CollectorItem
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCollectorItem extends EntityPathBase<CollectorItem> {

    private static final long serialVersionUID = -1182913378L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollectorItem collectorItem = new QCollectorItem("collectorItem");

    public final QBaseModel _super;

    public final QCollector collector;

    public final org.bson.types.QObjectId collectorId;

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    // inherited
    public final org.bson.types.QObjectId id;

    public final MapPath<String, Object, SimplePath<Object>> options = this.<String, Object, SimplePath<Object>>createMap("options", String.class, Object.class, SimplePath.class);

    public QCollectorItem(String variable) {
        this(CollectorItem.class, forVariable(variable), INITS);
    }

    public QCollectorItem(Path<? extends CollectorItem> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCollectorItem(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCollectorItem(PathMetadata<?> metadata, PathInits inits) {
        this(CollectorItem.class, metadata, inits);
    }

    public QCollectorItem(Class<? extends CollectorItem> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.collector = inits.isInitialized("collector") ? new QCollector(forProperty("collector"), inits.get("collector")) : null;
        this.collectorId = inits.isInitialized("collectorId") ? new org.bson.types.QObjectId(forProperty("collectorId")) : null;
        this.id = _super.id;
    }

}

