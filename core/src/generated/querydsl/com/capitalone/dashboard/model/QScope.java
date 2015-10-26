package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QScope is a Querydsl query type for Scope
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScope extends EntityPathBase<Scope> {

    private static final long serialVersionUID = 1435106226L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScope scope = new QScope("scope");

    public final QBaseModel _super;

    public final StringPath assetState = createString("assetState");

    public final StringPath beginDate = createString("beginDate");

    public final StringPath changeDate = createString("changeDate");

    public final org.bson.types.QObjectId collectorId;

    public final StringPath endDate = createString("endDate");

    // inherited
    public final org.bson.types.QObjectId id;

    public final StringPath isDeleted = createString("isDeleted");

    public final StringPath name = createString("name");

    public final StringPath pId = createString("pId");

    public final StringPath projectPath = createString("projectPath");

    public QScope(String variable) {
        this(Scope.class, forVariable(variable), INITS);
    }

    public QScope(Path<? extends Scope> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScope(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScope(PathMetadata<?> metadata, PathInits inits) {
        this(Scope.class, metadata, inits);
    }

    public QScope(Class<? extends Scope> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.collectorId = inits.isInitialized("collectorId") ? new org.bson.types.QObjectId(forProperty("collectorId")) : null;
        this.id = _super.id;
    }

}

