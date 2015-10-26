package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAuthentication is a Querydsl query type for Authentication
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAuthentication extends EntityPathBase<Authentication> {

    private static final long serialVersionUID = 1021136602L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthentication authentication = new QAuthentication("authentication");

    public final QBaseModel _super;

    // inherited
    public final org.bson.types.QObjectId id;

    public final StringPath password = createString("password");

    public final StringPath username = createString("username");

    public QAuthentication(String variable) {
        this(Authentication.class, forVariable(variable), INITS);
    }

    public QAuthentication(Path<? extends Authentication> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAuthentication(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAuthentication(PathMetadata<?> metadata, PathInits inits) {
        this(Authentication.class, metadata, inits);
    }

    public QAuthentication(Class<? extends Authentication> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.id = _super.id;
    }

}

