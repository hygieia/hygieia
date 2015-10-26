package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSCM is a Querydsl query type for SCM
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QSCM extends BeanPath<SCM> {

    private static final long serialVersionUID = -239848197L;

    public static final QSCM sCM = new QSCM("sCM");

    public final NumberPath<Long> numberOfChanges = createNumber("numberOfChanges", Long.class);

    public final StringPath scmAuthor = createString("scmAuthor");

    public final StringPath scmBranch = createString("scmBranch");

    public final StringPath scmCommitLog = createString("scmCommitLog");

    public final NumberPath<Long> scmCommitTimestamp = createNumber("scmCommitTimestamp", Long.class);

    public final StringPath scmRevisionNumber = createString("scmRevisionNumber");

    public final StringPath scmUrl = createString("scmUrl");

    public QSCM(String variable) {
        super(SCM.class, forVariable(variable));
    }

    public QSCM(Path<? extends SCM> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSCM(PathMetadata<?> metadata) {
        super(SCM.class, metadata);
    }

}

