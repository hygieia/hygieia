import {Component, Input, Type, ComponentFactoryResolver, ChangeDetectorRef, ViewRef} from '@angular/core';
import { IWidget } from '../interfaces';
import { BaseTemplateComponent } from '../templates/base-template/base-template.component';
import { TemplatesDirective } from '../templates/templates.directive';

@Component({
  template: '',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {

  @Input() baseTemplate: Type<any>;

  public widgets: IWidget[];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }

  loadComponent(templateTag: TemplatesDirective) {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.baseTemplate);
    const viewContainerRef = templateTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as BaseTemplateComponent).widgets = this.widgets;
    if (!(this.cdr as ViewRef).destroyed && this.cdr !== undefined) {
      this.cdr.detectChanges();
    }
  }
}
