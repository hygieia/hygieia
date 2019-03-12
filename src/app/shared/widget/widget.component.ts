import {
    Component, Input, ComponentFactoryResolver, ChangeDetectorRef, Type
} from '@angular/core';
import { LayoutComponent } from '../layout/layout.component';
import { LayoutDirective } from '../layout.directive';
import { Chart } from '../interfaces';

@Component({
    template: '',
    styleUrls: ['./widget.component.scss']
})
export class WidgetComponent {
    @Input() layout: Type<any>;

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


