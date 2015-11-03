package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QWidget is a Querydsl query type for Widget
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QWidget extends BeanPath<Widget> {

    private static final long serialVersionUID = 1658341542L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWidget widget = new QWidget("widget");

    public final org.bson.types.QObjectId componentId;

    public final org.bson.types.QObjectId id;

    public final StringPath name = createString("name");

    public final MapPath<String, Object, SimplePath<Object>> options = this.<String, Object, SimplePath<Object>>createMap("options", String.class, Object.class, SimplePath.class);

    public QWidget(String variable) {
        this(Widget.class, forVariable(variable), INITS);
    }

    public QWidget(Path<? extends Widget> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWidget(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWidget(PathMetadata<?> metadata, PathInits inits) {
        this(Widget.class, metadata, inits);
    }

    public QWidget(Class<? extends Widget> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.componentId = inits.isInitialized("componentId") ? new org.bson.types.QObjectId(forProperty("componentId")) : null;
        this.id = inits.isInitialized("id") ? new org.bson.types.QObjectId(forProperty("id")) : null;
    }

}

