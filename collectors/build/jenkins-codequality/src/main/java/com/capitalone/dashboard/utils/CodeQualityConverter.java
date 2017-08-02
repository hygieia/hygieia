package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.quality.CodeQualityVisitor;

public interface CodeQualityConverter {

  CodeQualityVisitor produceVisitor();
}
