import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  ElementRef,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { fromEvent, Observable, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

import { ChartDirective } from '../../charts/chart.directive';
import { LayoutComponent } from '../layout/layout.component';

@Component({
  selector: 'app-two-by-two-layout',
  templateUrl: './two-by-two-layout.component.html',
  styleUrls: ['./two-by-two-layout.component.scss']
})
export class TwoByTwoLayoutComponent extends LayoutComponent implements AfterViewInit {

  @ViewChildren(ChartDirective) childChartTags: QueryList<ChartDirective>;

  @ViewChildren('chartParent') chartContainers: QueryList<ElementRef>;

  constructor(componentFactoryResolver: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
    super(componentFactoryResolver, cdr);
  }

  resizeObservable$: Observable<Event>;
  resizeSubscription$: Subscription;

  // Initialize charts and resize hook
  ngAfterViewInit() {
    super.loadComponent(this.childChartTags);
    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.pipe(debounceTime(50)).subscribe(_ => {
      super.resize(this.chartContainers.toArray());
    });
    super.resize(this.chartContainers.toArray());
  }
}
