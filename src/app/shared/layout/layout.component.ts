import {
    Component, OnInit, Input, Type, ViewChildren, QueryList,
    ComponentFactoryResolver, ChangeDetectorRef, AfterViewInit
} from '@angular/core';
import { Chart } from '../chart';
import { ChartDirective } from '../chart.directive';
import { ChartComponent } from '../chart/chart.component';

@Component({
    template: '',
    styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {

    charts: Chart[];

    constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }

    loadComponent(chartTags: QueryList<ChartDirective>) {
        const chartTagArray = chartTags.toArray();
        for (let i = 0; i < chartTagArray.length && i < this.charts.length; i++) {
            const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.charts[i].component);
            const viewContainerRef = chartTagArray[i].viewContainerRef;
            viewContainerRef.clear();
            const componentRef = viewContainerRef.createComponent(componentFactory);
            (componentRef.instance as ChartComponent).data = this.charts[i].data;
        }

        this.cdr.detectChanges();
    }

}
