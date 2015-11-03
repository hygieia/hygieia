package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QEnvironmentStatus is a Querydsl query type for EnvironmentStatus
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QEnvironmentStatus extends EntityPathBase<EnvironmentStatus> {

    private static final long serialVersionUID = 1290564675L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEnvironmentStatus environmentStatus = new QEnvironmentStatus("environmentStatus");

    public final QBaseModel _super;

    public final org.bson.types.QObjectId collectorItemId;

    public final StringPath componentID = createString("componentID");

    public final StringPath componentName = createString("componentName");

    public final StringPath environmentName = createString("environmentName");

    // inherited
    public final org.bson.types.QObjectId id;

    public final BooleanPath online = createBoolean("online");

    public final StringPath parentAgentName = createString("parentAgentName");

    public final StringPath resourceName = createString("resourceName");

    public QEnvironmentStatus(String variable) {
        this(EnvironmentStatus.class, forVariable(variable), INITS);
    }

    public QEnvironmentStatus(Path<? extends EnvironmentStatus> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEnvironmentStatus(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEnvironmentStatus(PathMetadata<?> metadata, PathInits inits) {
        this(EnvironmentStatus.class, metadata, inits);
    }

    public QEnvironmentStatus(Class<? extends EnvironmentStatus> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.collectorItemId = inits.isInitialized("collectorItemId") ? new org.bson.types.QObjectId(forProperty("collectorItemId")) : null;
        this.id = _super.id;
    }

}

