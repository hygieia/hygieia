package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCommit is a Querydsl query type for Commit
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCommit extends EntityPathBase<Commit> {

    private static final long serialVersionUID = 1091573657L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommit commit = new QCommit("commit");

    public final QSCM _super = new QSCM(this);

    public final org.bson.types.QObjectId collectorItemId;

    public final org.bson.types.QObjectId id;

    //inherited
    public final NumberPath<Long> numberOfChanges = _super.numberOfChanges;

    //inherited
    public final StringPath scmAuthor = _super.scmAuthor;

    //inherited
    public final StringPath scmBranch = _super.scmBranch;

    //inherited
    public final StringPath scmCommitLog = _super.scmCommitLog;

    //inherited
    public final NumberPath<Long> scmCommitTimestamp = _super.scmCommitTimestamp;

    //inherited
    public final StringPath scmRevisionNumber = _super.scmRevisionNumber;

    //inherited
    public final StringPath scmUrl = _super.scmUrl;

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public QCommit(String variable) {
        this(Commit.class, forVariable(variable), INITS);
    }

    public QCommit(Path<? extends Commit> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommit(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommit(PathMetadata<?> metadata, PathInits inits) {
        this(Commit.class, metadata, inits);
    }

    public QCommit(Class<? extends Commit> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collectorItemId = inits.isInitialized("collectorItemId") ? new org.bson.types.QObjectId(forProperty("collectorItemId")) : null;
        this.id = inits.isInitialized("id") ? new org.bson.types.QObjectId(forProperty("id")) : null;
    }

}

