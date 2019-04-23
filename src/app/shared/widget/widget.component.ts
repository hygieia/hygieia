import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, Type } from '@angular/core';

import { Chart } from '../interfaces';
import { LayoutDirective } from '../layouts/layout.directive';
import { LayoutComponent } from '../layouts/layout/layout.component';

@Component({
  template: '',
  styleUrls: ['./widget.component.scss']
})
export class WidgetComponent {
  @Input() layout: Type<any>;
  @Input() status : string;


  public charts: Chart[];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }

  loadComponent(layoutTag: LayoutDirective) {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.layout);
    const viewContainerRef = layoutTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as LayoutComponent).charts = this.charts;
    this.cdr.detectChanges();
  }


}


