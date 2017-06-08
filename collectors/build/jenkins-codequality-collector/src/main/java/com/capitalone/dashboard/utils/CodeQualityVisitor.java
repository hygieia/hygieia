package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.FindBubsXmlReport;
import com.capitalone.dashboard.model.JunitXmlReport;

public interface CodeQualityVisitor {

    CodeQuality produceResult();

    void visit(JunitXmlReport junitXmlReport);

    void visit(FindBubsXmlReport findBubsXmlReport);
}
