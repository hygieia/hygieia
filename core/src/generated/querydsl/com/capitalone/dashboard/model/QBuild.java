package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QBuild is a Querydsl query type for Build
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBuild extends EntityPathBase<Build> {

    private static final long serialVersionUID = 1419936716L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBuild build = new QBuild("build");

    public final QBaseModel _super;

    public final StringPath artifactVersionNumber = createString("artifactVersionNumber");

    public final EnumPath<BuildStatus> buildStatus = createEnum("buildStatus", BuildStatus.class);

    public final StringPath buildUrl = createString("buildUrl");

    public final org.bson.types.QObjectId collectorItemId;

    public final NumberPath<Long> duration = createNumber("duration", Long.class);

    public final NumberPath<Long> endTime = createNumber("endTime", Long.class);

    // inherited
    public final org.bson.types.QObjectId id;

    public final StringPath log = createString("log");

    public final StringPath number = createString("number");

    public final ListPath<SCM, QSCM> sourceChangeSet = this.<SCM, QSCM>createList("sourceChangeSet", SCM.class, QSCM.class, PathInits.DIRECT2);

    public final StringPath startedBy = createString("startedBy");

    public final NumberPath<Long> startTime = createNumber("startTime", Long.class);

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public QBuild(String variable) {
        this(Build.class, forVariable(variable), INITS);
    }

    public QBuild(Path<? extends Build> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBuild(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBuild(PathMetadata<?> metadata, PathInits inits) {
        this(Build.class, metadata, inits);
    }

    public QBuild(Class<? extends Build> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.collectorItemId = inits.isInitialized("collectorItemId") ? new org.bson.types.QObjectId(forProperty("collectorItemId")) : null;
        this.id = _super.id;
    }

}

