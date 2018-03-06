package com.capitalone.dashboard.widget;

import com.capitalone.dashboard.model.score.settings.ScoreComponentSettings;
import com.capitalone.dashboard.model.ScoreWeight;
import com.capitalone.dashboard.model.Widget;

public interface WidgetScore {

  /**
   * Process score for a widget
   *
   * @param widget widget configuration from dashboard
   * @param paramSettings Score Settings for the widget
   * @return Score for widget
   */
  ScoreWeight processWidgetScore(Widget widget, ScoreComponentSettings paramSettings);

}
