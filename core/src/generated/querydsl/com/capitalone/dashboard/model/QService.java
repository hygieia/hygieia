package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QService is a Querydsl query type for Service
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QService extends EntityPathBase<Service> {

    private static final long serialVersionUID = 512795795L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QService service = new QService("service");

    public final QBaseModel _super;

    public final StringPath applicationName = createString("applicationName");

    public final org.bson.types.QObjectId dashboardId;

    public final SetPath<org.bson.types.ObjectId, org.bson.types.QObjectId> dependedBy = this.<org.bson.types.ObjectId, org.bson.types.QObjectId>createSet("dependedBy", org.bson.types.ObjectId.class, org.bson.types.QObjectId.class, PathInits.DIRECT2);

    // inherited
    public final org.bson.types.QObjectId id;

    public final NumberPath<Long> lastUpdated = createNumber("lastUpdated", Long.class);

    public final StringPath message = createString("message");

    public final StringPath name = createString("name");

    public final EnumPath<ServiceStatus> status = createEnum("status", ServiceStatus.class);

    public QService(String variable) {
        this(Service.class, forVariable(variable), INITS);
    }

    public QService(Path<? extends Service> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QService(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QService(PathMetadata<?> metadata, PathInits inits) {
        this(Service.class, metadata, inits);
    }

    public QService(Class<? extends Service> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.dashboardId = inits.isInitialized("dashboardId") ? new org.bson.types.QObjectId(forProperty("dashboardId")) : null;
        this.id = _super.id;
    }

}

