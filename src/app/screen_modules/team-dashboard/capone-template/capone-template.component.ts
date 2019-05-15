import { Component, AfterViewInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChildren, QueryList } from '@angular/core';
import { WidgetDirective } from 'src/app/shared/widget/widget.directive';
import {BaseTemplateComponent} from '../../../shared/templates/base-template/base-template.component';
import {WidgetHeaderComponent} from '../../../shared/widget-header/widget-header.component';

@Component({
  selector: 'app-capone-template',
  templateUrl: './capone-template.component.html',
  styleUrls: ['./capone-template.component.scss']
})
export class CaponeTemplateComponent extends BaseTemplateComponent implements AfterViewInit {

  @ViewChildren(WidgetDirective) childWidgetTags: QueryList<WidgetDirective>;
  constructor( componentResolverFactory: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
    super(componentResolverFactory, cdr);
  }

  ngAfterViewInit() {
    // super.loadComponent(this.childWidgetTags);
    // super.loadComponent(WidgetHeaderComponent);
  }

}
