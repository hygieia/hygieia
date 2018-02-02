package com.capitalone.dashboard.widget;

import com.capitalone.dashboard.collector.ScoreParamSettings;
import com.capitalone.dashboard.model.ScoreWeight;
import com.capitalone.dashboard.model.Widget;

public interface WidgetScore {

  ScoreWeight processWidgetScore(Widget widget, ScoreParamSettings paramSettings);

}
