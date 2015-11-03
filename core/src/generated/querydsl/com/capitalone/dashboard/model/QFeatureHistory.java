package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QFeatureHistory is a Querydsl query type for FeatureHistory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFeatureHistory extends EntityPathBase<FeatureHistory> {

    private static final long serialVersionUID = -544256736L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeatureHistory featureHistory = new QFeatureHistory("featureHistory");

    public final QBaseModel _super;

    public final StringPath changeDate = createString("changeDate");

    // inherited
    public final org.bson.types.QObjectId id;

    public final StringPath isDeleted = createString("isDeleted");

    public final StringPath projectID = createString("projectID");

    public final StringPath reportedDate = createString("reportedDate");

    public final StringPath sAssetState = createString("sAssetState");

    public final StringPath sEstimate = createString("sEstimate");

    public final StringPath sprintBeginDate = createString("sprintBeginDate");

    public final StringPath sprintEndDate = createString("sprintEndDate");

    public final StringPath sprintID = createString("sprintID");

    public final StringPath sSoftwareTesting = createString("sSoftwareTesting");

    public final StringPath sStatus = createString("sStatus");

    public final StringPath sToDo = createString("sToDo");

    public final StringPath storyID = createString("storyID");

    public final StringPath teamID = createString("teamID");

    public QFeatureHistory(String variable) {
        this(FeatureHistory.class, forVariable(variable), INITS);
    }

    public QFeatureHistory(Path<? extends FeatureHistory> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFeatureHistory(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFeatureHistory(PathMetadata<?> metadata, PathInits inits) {
        this(FeatureHistory.class, metadata, inits);
    }

    public QFeatureHistory(Class<? extends FeatureHistory> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.id = _super.id;
    }

}

