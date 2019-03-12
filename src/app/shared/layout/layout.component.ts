import {
    Component, OnInit, Input, Type, ViewChildren, QueryList,
    ComponentFactoryResolver, ChangeDetectorRef, AfterViewInit
} from '@angular/core';
import { ChartDirective } from '../chart.directive';
import { ChartComponent } from '../chart/chart.component';
import { Chart } from '../interfaces';

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
            const chartComponent = (componentRef.instance as ChartComponent);
            chartComponent.data = this.charts[i].data;
            chartComponent.xAxisLabel = this.charts[i].xAxisLabel;
            chartComponent.yAxisLabel = this.charts[i].yAxisLabel;
            chartComponent.colorScheme = this.charts[i].colorScheme;
        }

        this.cdr.detectChanges();
    }

}
