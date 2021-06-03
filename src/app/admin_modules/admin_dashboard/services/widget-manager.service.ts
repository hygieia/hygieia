import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WidgetManagerService {

  widgets = {};

  constructor() { }


  register(widgetName, options) {
    widgetName = widgetName.toLowerCase();

    // don't allow widgets to be registered twice
    if (this.widgets[widgetName]) {
      throw new Error(widgetName + ' already registered!');
    }

    // make sure certain values are set
    if (!options.view || !options.view.controller || !options.view.templateUrl) {
      throw new Error(widgetName + ' must be registered with the controller, and templateUrl values defined');
    }

    this.widgets[widgetName] = options;
  }

  getWidgets() {
    return this.widgets;
  }

  getWidget(widgetName) {
    widgetName = widgetName.toLowerCase();

    return this.widgets[widgetName];
  }
}

