package com.capitalone.dashboard.model.quality;

import com.capitalone.dashboard.model.CodeQuality;

public interface CodeQualityVisitor {

    CodeQuality produceResult();

    void visit(JunitXmlReport junitXmlReport);

    void visit(FindBugsXmlReport findBugsXmlReport);

    void visit(JacocoXmlReport jacocoXmlReport);

    void visit(PmdReport pmdReport);

    void visit(CheckstyleReport checkstyleReport);
}
