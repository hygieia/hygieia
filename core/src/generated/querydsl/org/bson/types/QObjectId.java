package org.bson.types;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QObjectId is a Querydsl query type for ObjectId
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QObjectId extends BeanPath<ObjectId> {

    private static final long serialVersionUID = 1931139171L;

    public static final QObjectId objectId = new QObjectId("objectId");

    public final NumberPath<Integer> _inc = createNumber("_inc", Integer.class);

    public final NumberPath<Integer> _machine = createNumber("_machine", Integer.class);

    public final BooleanPath _new = createBoolean("_new");

    public final NumberPath<Integer> _time = createNumber("_time", Integer.class);

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final NumberPath<Integer> inc = createNumber("inc", Integer.class);

    public final NumberPath<Integer> machine = createNumber("machine", Integer.class);

    public final BooleanPath new$ = createBoolean("new");

    public final NumberPath<Long> time = createNumber("time", Long.class);

    public final NumberPath<Integer> timeSecond = createNumber("timeSecond", Integer.class);

    public final NumberPath<Integer> timestamp = createNumber("timestamp", Integer.class);

    public QObjectId(String variable) {
        super(ObjectId.class, forVariable(variable));
    }

    public QObjectId(Path<? extends ObjectId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QObjectId(PathMetadata<?> metadata) {
        super(ObjectId.class, metadata);
    }

}

