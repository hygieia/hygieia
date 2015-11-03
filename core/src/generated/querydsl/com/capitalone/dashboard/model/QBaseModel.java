package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QBaseModel is a Querydsl query type for BaseModel
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QBaseModel extends BeanPath<BaseModel> {

    private static final long serialVersionUID = 1531477302L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBaseModel baseModel = new QBaseModel("baseModel");

    public final org.bson.types.QObjectId id;

    public QBaseModel(String variable) {
        this(BaseModel.class, forVariable(variable), INITS);
    }

    public QBaseModel(Path<? extends BaseModel> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBaseModel(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBaseModel(PathMetadata<?> metadata, PathInits inits) {
        this(BaseModel.class, metadata, inits);
    }

    public QBaseModel(Class<? extends BaseModel> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new org.bson.types.QObjectId(forProperty("id")) : null;
    }

}

