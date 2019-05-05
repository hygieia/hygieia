import { ChangeDetectorRef, Component, ComponentFactoryResolver, QueryList } from '@angular/core';

import { ChartDirective } from '../../charts/chart.directive';
import { ChartComponent } from '../../charts/chart/chart.component';
import { IChart } from '../../interfaces';

@Component({
  template: '',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {

  charts: IChart[];

  chartComponents: ChartComponent[] = [];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, protected cdr: ChangeDetectorRef) { }

  loadComponent(chartTags: QueryList<ChartDirective>) {
    const chartTagArray = chartTags.toArray();
    for (let i = 0; i < chartTagArray.length && i < this.charts.length; i++) {
      const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.charts[i].component);
      const viewContainerRef = chartTagArray[i].viewContainerRef;
      viewContainerRef.clear();
      const componentRef = viewContainerRef.createComponent(componentFactory);
      const chartComponent = (componentRef.instance as ChartComponent);
      this.chartComponents.push(chartComponent);
      chartComponent.title = this.charts[i].title;
      chartComponent.data = this.charts[i].data;
      chartComponent.xAxisLabel = this.charts[i].xAxisLabel;
      chartComponent.yAxisLabel = this.charts[i].yAxisLabel;
      chartComponent.colorScheme = this.charts[i].colorScheme;
    }

    this.cdr.detectChanges();
  }

}
