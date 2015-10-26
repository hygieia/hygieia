package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QCodeQualityMetric is a Querydsl query type for CodeQualityMetric
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QCodeQualityMetric extends BeanPath<CodeQualityMetric> {

    private static final long serialVersionUID = 1567986432L;

    public static final QCodeQualityMetric codeQualityMetric = new QCodeQualityMetric("codeQualityMetric");

    public final StringPath formattedValue = createString("formattedValue");

    public final StringPath name = createString("name");

    public final EnumPath<CodeQualityMetricStatus> status = createEnum("status", CodeQualityMetricStatus.class);

    public final StringPath statusMessage = createString("statusMessage");

    public final SimplePath<Object> value = createSimple("value", Object.class);

    public QCodeQualityMetric(String variable) {
        super(CodeQualityMetric.class, forVariable(variable));
    }

    public QCodeQualityMetric(Path<? extends CodeQualityMetric> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCodeQualityMetric(PathMetadata<?> metadata) {
        super(CodeQualityMetric.class, metadata);
    }

}

