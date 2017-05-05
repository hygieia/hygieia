package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.FindBugsXmlReport;
import com.capitalone.dashboard.model.JacocoXmlReport;
import com.capitalone.dashboard.model.JunitXmlReport;

public interface CodeQualityVisitor {

    CodeQuality produceResult();

    void visit(JunitXmlReport junitXmlReport);

    void visit(FindBugsXmlReport findBugsXmlReport);

    void visit(JacocoXmlReport jacocoXmlReport);
}
