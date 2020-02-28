import { Component, AfterViewInit, ChangeDetectorRef, ComponentFactoryResolver, ElementRef, QueryList, ViewChildren, ViewRef, } from '@angular/core';
import { fromEvent, Observable, Subscription} from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ChartDirective } from '../../charts/chart.directive';
import { LayoutComponent } from '../layout/layout.component';

@Component({
  selector: 'app-one-chart-layout',
  templateUrl: './one-chart-layout.component.html',
  styleUrls: ['./one-chart-layout.component.scss']
})

export class OneChartLayoutComponent extends LayoutComponent implements AfterViewInit {
  @ViewChildren(ChartDirective) childChartTags: QueryList<ChartDirective>;
  @ViewChildren('chartParent') chartContainers: QueryList<ElementRef>;

  constructor(componentFactoryResolver: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
    super(componentFactoryResolver, cdr);
  }

  resizeObservable$: Observable<Event>;
  resizeSubscription$: Subscription;

  ngAfterViewInit() {
    super.loadComponent(this.childChartTags);
    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.pipe(debounceTime(50)).subscribe(_ => {
      this.resize();
    });
    this.resize();
  }

  resize() {
    const chartContainerArray = this.chartContainers.toArray();
    chartContainerArray.forEach((currChartContainer, index) => {
      const currChartComponent = this.chartComponents[index];
      const width = currChartContainer.nativeElement.getBoundingClientRect().width;
      if (currChartComponent.scaleFactor) {
        currChartComponent.view = [width, width * currChartComponent.scaleFactor];
      } else {
        currChartComponent.view = [width, width * .4];
      }
    });

    if (!(this.cdr as ViewRef).destroyed && this.cdr !== undefined) {
      this.cdr.detectChanges();
    }
  }
}

