import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  ElementRef,
  QueryList,
  ViewChildren,
  ViewRef,
} from '@angular/core';
import { fromEvent, Observable, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ChartDirective } from '../../charts/chart.directive';
import { LayoutComponent } from '../layout/layout.component';

@Component({
  selector: 'app-one-by-two-layout',
  templateUrl: './one-by-two-layout.component.html',
  styleUrls: ['./one-by-two-layout.component.scss']
})
export class OneByTwoLayoutComponent extends LayoutComponent implements AfterViewInit {

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
      this.resize();
    });
    this.resize();
  }

  // Support chart resizing based on parent containers.
  // Size height based on ratio with width of chart.
  resize() {
    const chartContainerArray = this.chartContainers.toArray();
    for (let i = 0; i < chartContainerArray.length && i < this.chartComponents.length; i++) {
      const width = chartContainerArray[i].nativeElement.getBoundingClientRect().width;
      if (this.chartComponents[i].scaleFactor) {
        this.chartComponents[i].view = [width, width * this.chartComponents[i].scaleFactor];
      } else {
        this.chartComponents[i].view = [width, width * .4];
      }
    }
    if (!(this.cdr as ViewRef).destroyed) {
      this.cdr.detectChanges();
    }
  }



}
