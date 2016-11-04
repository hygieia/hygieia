package com.capitalone.dashboard.model;

import com.capitalone.dashboard.utils.CodeQualityVisitor;

public class FindBubsXmlReport implements CodeQualityVisitee {
    @Override
    public void accept(CodeQualityVisitor visitor) {
        visitor.visit(this);
    }
}
