package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QEnvironmentComponent is a Querydsl query type for EnvironmentComponent
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QEnvironmentComponent extends EntityPathBase<EnvironmentComponent> {

    private static final long serialVersionUID = -664614132L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEnvironmentComponent environmentComponent = new QEnvironmentComponent("environmentComponent");

    public final QBaseModel _super;

    public final NumberPath<Long> asOfDate = createNumber("asOfDate", Long.class);

    public final org.bson.types.QObjectId collectorItemId;

    public final StringPath componentID = createString("componentID");

    public final StringPath componentName = createString("componentName");

    public final StringPath componentVersion = createString("componentVersion");

    public final BooleanPath deployed = createBoolean("deployed");

    public final StringPath environmentName = createString("environmentName");

    public final StringPath environmentUrl = createString("environmentUrl");

    // inherited
    public final org.bson.types.QObjectId id;

    public QEnvironmentComponent(String variable) {
        this(EnvironmentComponent.class, forVariable(variable), INITS);
    }

    public QEnvironmentComponent(Path<? extends EnvironmentComponent> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEnvironmentComponent(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEnvironmentComponent(PathMetadata<?> metadata, PathInits inits) {
        this(EnvironmentComponent.class, metadata, inits);
    }

    public QEnvironmentComponent(Class<? extends EnvironmentComponent> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.collectorItemId = inits.isInitialized("collectorItemId") ? new org.bson.types.QObjectId(forProperty("collectorItemId")) : null;
        this.id = _super.id;
    }

}

