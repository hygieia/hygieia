package com.capitalone.dashboard.model;

import com.capitalone.dashboard.utils.CodeQualityVisitor;

public interface CodeQualityVisitee {

    void accept(CodeQualityVisitor visitor);
}
