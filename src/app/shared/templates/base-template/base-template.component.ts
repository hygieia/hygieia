import { Component, ComponentFactoryResolver, ChangeDetectorRef, QueryList } from '@angular/core';
import { IWidget } from '../../interfaces';
import { WidgetDirective } from '../../widget/widget.directive';
import { WidgetComponent } from '../../widget/widget.component';

@Component({
  template: '',
  styleUrls: ['./base-template.component.scss']
})
export class BaseTemplateComponent  {

  widgets: IWidget[];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }

  loadComponent(widgetTags: QueryList<WidgetDirective>) {
    const widgetTagArray = widgetTags.toArray();
    for (let i = 0; i < widgetTagArray.length && i < this.widgets.length; i++) {
      const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.widgets[i].component);
      const viewContainerRef = widgetTagArray[i].viewContainerRef;
      viewContainerRef.clear();
      const componentRef = viewContainerRef.createComponent(componentFactory);
      const widgetComponent = (componentRef.instance as WidgetComponent);
      widgetComponent.status = this.widgets[i].status;

    }
    this.cdr.detectChanges();
  }


}
