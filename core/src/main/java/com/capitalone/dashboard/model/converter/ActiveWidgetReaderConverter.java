package com.capitalone.dashboard.model.converter;

import com.capitalone.dashboard.model.ActiveWidget;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Created by stevegal on 27/10/2018.
 * Allow old dashboards to be supported (where title and type are the same)
 */
@ReadingConverter
public class ActiveWidgetReaderConverter implements Converter<String,ActiveWidget> {
  @Override
  public ActiveWidget convert(String s) {
    return ActiveWidget.newActiveWidget().type(s).title(s).build();
  }
}
