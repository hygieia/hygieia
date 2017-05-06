package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;

public interface CodeQualityVisitor {

    CodeQuality produceResult();

    void visit(JunitXmlReport junitXmlReport);

    void visit(FindBugsXmlReport findBugsXmlReport);

    void visit(JacocoXmlReport jacocoXmlReport);

    void visit(PmdReport pmdReport);

    void visit(CheckstyleReport checkstyleReport);
}
