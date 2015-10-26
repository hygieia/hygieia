package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QScopeOwner is a Querydsl query type for ScopeOwner
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScopeOwner extends EntityPathBase<ScopeOwner> {

    private static final long serialVersionUID = 1019905569L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScopeOwner scopeOwner = new QScopeOwner("scopeOwner");

    public final QCollectorItem _super;

    public final StringPath assetState = createString("assetState");

    public final StringPath changeDate = createString("changeDate");

    // inherited
    public final QCollector collector;

    // inherited
    public final org.bson.types.QObjectId collectorId;

    public final org.bson.types.QObjectId collectorItemId;

    //inherited
    public final StringPath description;

    //inherited
    public final BooleanPath enabled;

    // inherited
    public final org.bson.types.QObjectId id;

    public final StringPath isDeleted = createString("isDeleted");

    public final StringPath name = createString("name");

    //inherited
    public final MapPath<String, Object, SimplePath<Object>> options;

    public final StringPath teamId = createString("teamId");

    public QScopeOwner(String variable) {
        this(ScopeOwner.class, forVariable(variable), INITS);
    }

    public QScopeOwner(Path<? extends ScopeOwner> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScopeOwner(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScopeOwner(PathMetadata<?> metadata, PathInits inits) {
        this(ScopeOwner.class, metadata, inits);
    }

    public QScopeOwner(Class<? extends ScopeOwner> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QCollectorItem(type, metadata, inits);
        this.collector = _super.collector;
        this.collectorId = _super.collectorId;
        this.collectorItemId = inits.isInitialized("collectorItemId") ? new org.bson.types.QObjectId(forProperty("collectorItemId")) : null;
        this.description = _super.description;
        this.enabled = _super.enabled;
        this.id = _super.id;
        this.options = _super.options;
    }

}

