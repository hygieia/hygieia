package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCollector is a Querydsl query type for Collector
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCollector extends EntityPathBase<Collector> {

    private static final long serialVersionUID = 953199339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollector collector = new QCollector("collector");

    public final QBaseModel _super;

    public final EnumPath<CollectorType> collectorType = createEnum("collectorType", CollectorType.class);

    public final BooleanPath enabled = createBoolean("enabled");

    // inherited
    public final org.bson.types.QObjectId id;

    public final NumberPath<Long> lastExecuted = createNumber("lastExecuted", Long.class);

    public final StringPath name = createString("name");

    public final BooleanPath online = createBoolean("online");

    public QCollector(String variable) {
        this(Collector.class, forVariable(variable), INITS);
    }

    public QCollector(Path<? extends Collector> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCollector(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCollector(PathMetadata<?> metadata, PathInits inits) {
        this(Collector.class, metadata, inits);
    }

    public QCollector(Class<? extends Collector> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QBaseModel(type, metadata, inits);
        this.id = _super.id;
    }

}

