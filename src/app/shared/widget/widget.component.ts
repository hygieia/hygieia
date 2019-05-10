import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, Type } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { extend } from 'lodash';
import { Observable, ReplaySubject, zip } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';

import { DashboardService } from '../dashboard.service';
import { IChart } from '../interfaces';
import { LayoutDirective } from '../layouts/layout.directive';
import { LayoutComponent } from '../layouts/layout/layout.component';


@Component({
  template: '',
  styleUrls: ['./widget.component.scss']
})
export class WidgetComponent {

  private dashboardId: string;

  @Input() widgetId: string;

  @Input() layout: Type<any>;

  public charts: IChart[];

  // Subjects can subscribe and emit. This allows us
  // to subsribe to updates from the upstream dashboard
  // config but also trigger updates for only this widget
  // if needed.
  protected widgetConfigSubject = new ReplaySubject<any>(1);

  // Wrap the subject as an observable for outside users.
  public widgetConfig$ = this.widgetConfigSubject.asObservable();

  constructor(private componentFactoryResolver: ComponentFactoryResolver,
              private cdr: ChangeDetectorRef,
              private dashboardService: DashboardService,
              private route: ActivatedRoute) { }


  // Pull the layout tag from the template and initialize the charts
  // in the widget. Should be called after chart data is set.
  loadComponent(layoutTag: LayoutDirective): void {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.layout);
    const viewContainerRef = layoutTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as LayoutComponent).charts = this.charts;
    this.cdr.detectChanges();
  }

  // Pipe the widget config observable to limit
  // to one. This ends subscriptions after 1 event.
  getCurrentWidgetConfig(): Observable<any> {
    return this.widgetConfig$.pipe(take(1));
  }

  // Implementation specific to widget subclass.
  // Specific refresh logic for each widget should be
  // implemented in each subclass.
  startRefreshInterval() {
  }

  updateWidgetConfig(newWidgetConfig: any): void {
    if (!newWidgetConfig) {
      return;
    }

    // Take the current config and prepare it for saving
    const newWidgetConfig$ = this.getCurrentWidgetConfig().pipe(
      map(widgetConfig => {
        extend(widgetConfig, newWidgetConfig);
        return widgetConfig;
      }),
      map(widgetConfig => {
        if (widgetConfig.collectorItemId) {
          widgetConfig.collectorItemIds = [widgetConfig.collectorItemId];
          delete widgetConfig.collectorItemId;
        }
        return widgetConfig;
      }),
    );

    // Take the modified widgetConfig and upsert it.
    const upsertDashboardResult$ = newWidgetConfig$.pipe(
      switchMap(widgetConfig => {
        return this.dashboardService.upsertWidget(this.dashboardId, widgetConfig);
      }));

    // Take the new widget and the results from the API call
    // and have the dashboard service take this data to
    // publish the new config.
    zip(newWidgetConfig$, upsertDashboardResult$).pipe(
      map(([widgetConfig, upsertWidgetResponse]) => ({ widgetConfig, upsertWidgetResponse }))
    ).subscribe(result => {
      if (result.widgetConfig !== null && typeof result.widgetConfig === 'object') {
        extend(result.widgetConfig, result.upsertWidgetResponse.widget);
      }

      this.dashboardService.upsertLocally(result.upsertWidgetResponse.component, result.widgetConfig);

      // Push the new config to the widget, which
      // will trigger whatever is subscribed to
      // widgetConfig$
      this.widgetConfigSubject.next(result.widgetConfig);
      this.startRefreshInterval();
    });
  }

  // Take one dashboard config from the dashboard service.
  // Load dashboard will initiate the http request that
  // this subscription will receive. The dashboard config
  // is filtered for this widget and pushed to the local
  // config subject.
  init(): void {
    this.dashboardService.dashboardConfig$.pipe(
      map(result => this.findWidget(result.widgets)),
      take(1)
    ).subscribe(result => this.widgetConfigSubject.next(result));

    // TODO: Temporary test routing until dashboard template is integrated
    // Pass the dashboard id to view the build charts for that dashboard
    this.dashboardId = this.route.snapshot.paramMap.get('id');
    this.dashboardService.loadDashboard('596acb685797b408c8f51e8d');
    // this.dashboardService.loadDashboard(this.dashboardId);
  }

  // Find the widget config from the list of widgets
  findWidget(widgets: any[]): any {
    return widgets.find(widget => widget.options && widget.options.id === this.widgetId);
  }
}


