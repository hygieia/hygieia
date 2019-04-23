import { Component, AfterViewInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChildren, QueryList } from '@angular/core';
import { WidgetDirective } from 'src/app/shared/widget/widget.directive';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import {BaseTemplateComponent} from "../../../shared/templates/base-template/base-template.component";

@Component({
  selector: 'app-capone-template',
  templateUrl: './capone-template.component.html',
  styleUrls: ['./capone-template.component.scss']
})
export class CaponeTemplateComponent extends BaseTemplateComponent implements AfterViewInit {

  @ViewChildren(WidgetDirective) childWidgetTags: QueryList<WidgetDirective>;
  constructor( componentResolverFacotry: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
    super(componentResolverFacotry,cdr);
  }

  ngAfterViewInit(){
    super.loadComponent(this.childWidgetTags);
  }

}
