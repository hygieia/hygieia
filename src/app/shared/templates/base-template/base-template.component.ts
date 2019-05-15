import { ChangeDetectorRef, Component, ComponentFactoryResolver, QueryList } from '@angular/core';

import { IWidget } from '../../interfaces';
import { WidgetComponent } from '../../widget/widget.component';
import { WidgetDirective } from '../../widget/widget.directive';
import {WidgetHeaderComponent} from '../../widget-header/widget-header.component';
import {WidgetHeaderDirective} from '../../widget-header/widget-header.directive';

@Component({
  template: '',
  styleUrls: ['./base-template.component.scss']
})
export class BaseTemplateComponent  {

  widgets: IWidget[];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }


  // loadComponent(widgetHeaderTags: QueryList<WidgetHeaderDirective>) {
  //   const widgetTagArray = widgetHeaderTags.toArray();
  //   for (let i = 0; i < widgetTagArray.length && i < this.widgets.length; i++) {
  //     // const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.widgets[i].component);
  //
  //     // load header for widget type
  //     const componentFactory = this.componentFactoryResolver.resolveComponentFactory(WidgetHeaderComponent);
  //     const viewContainerRef = widgetTagArray[i].viewContainerRef;
  //     viewContainerRef.clear();
  //     const componentRef = viewContainerRef.createComponent(componentFactory);
  //
  //     // specify widget component to load in header
  //     const widgetHeaderComponent = (componentRef.instance as WidgetHeaderComponent);
  //     // widgetComponent.status = this.widgets[i].status;
  //     widgetHeaderComponent.widgetType = this.widgets[i].component;
  //   }
  //   this.cdr.detectChanges();
  // }


}
