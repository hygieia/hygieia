package com.capitalone.dashboard.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTestSuite is a Querydsl query type for TestSuite
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QTestSuite extends BeanPath<TestSuite> {

    private static final long serialVersionUID = -2121462812L;

    public static final QTestSuite testSuite = new QTestSuite("testSuite");

    public final StringPath description = createString("description");

    public final NumberPath<Long> duration = createNumber("duration", Long.class);

    public final NumberPath<Long> endTime = createNumber("endTime", Long.class);

    public final NumberPath<Integer> errorCount = createNumber("errorCount", Integer.class);

    public final NumberPath<Integer> failureCount = createNumber("failureCount", Integer.class);

    public final NumberPath<Integer> skippedCount = createNumber("skippedCount", Integer.class);

    public final NumberPath<Long> startTime = createNumber("startTime", Long.class);

    public final CollectionPath<TestCase, SimplePath<TestCase>> testCases = this.<TestCase, SimplePath<TestCase>>createCollection("testCases", TestCase.class, SimplePath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> totalCount = createNumber("totalCount", Integer.class);

    public final EnumPath<TestSuiteType> type = createEnum("type", TestSuiteType.class);

    public QTestSuite(String variable) {
        super(TestSuite.class, forVariable(variable));
    }

    public QTestSuite(Path<? extends TestSuite> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestSuite(PathMetadata<?> metadata) {
        super(TestSuite.class, metadata);
    }

}

