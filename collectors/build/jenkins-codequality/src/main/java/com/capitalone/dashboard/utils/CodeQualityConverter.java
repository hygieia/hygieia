package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.quality.QualityVisitor;

public interface CodeQualityConverter {

  QualityVisitor<CodeQuality> produceVisitor();
}
