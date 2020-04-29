import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type, ViewChild} from '@angular/core';
import {map, switchMap, take} from 'rxjs/operators';
import {zip} from 'rxjs';
import { extend } from 'lodash';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {IAuditResult, IWidgetConfigResponse} from '../interfaces';
import {ConfirmationModalComponent} from '../modals/confirmation-modal/confirmation-modal.component';
import {FormModalComponent} from '../modals/form-modal/form-modal.component';
import {WidgetComponent} from '../widget/widget.component';
import {WidgetDirective} from '../widget/widget.directive';
import {DashboardService} from '../dashboard.service';
import {AuditModalComponent} from '../modals/audit-modal/audit-modal.component';

@Component({
  selector: 'app-widget-header',
  templateUrl: './widget-header.component.html',
  styleUrls: ['./widget-header.component.scss']
})

export class WidgetHeaderComponent implements OnInit {

  @Input() widgetType: Type<any>;
  @Input() title;
  @Input() status;
  @Input() configForm: Type<any>;
  @ViewChild(WidgetDirective, {static: true}) appWidget: WidgetDirective;
  private widgetComponent;
  private auditStatus: string;
  private auditResult: IAuditResult;

  constructor(private componentFactoryResolver: ComponentFactoryResolver,
              private cdr: ChangeDetectorRef,
              private modalService: NgbModal,
              private dashboardService: DashboardService) {
  }

  ngOnInit() {
    this.loadComponent();
  }

  loadComponent() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.widgetType);
    if (this.appWidget !== undefined) {
      const viewContainerRef = this.appWidget.viewContainerRef;
      viewContainerRef.clear();
      const componentRef = viewContainerRef.createComponent(componentFactory);
      this.widgetComponent = (componentRef.instance as WidgetComponent);
      this.widgetComponent.status = status;
    }
    this.detectChanges();
    if (this.widgetComponent) {
      this.findWidgetAuditStatus(this.widgetComponent.auditType);
    }
  }

  // Open the config modal and pass it necessary data. When it is closed pass the results to update them.
  openConfig() {
    const modalRef = this.modalService.open(FormModalComponent);
    if (!modalRef) {
      return;
    }
    modalRef.componentInstance.title = 'Configure';
    modalRef.componentInstance.form = this.configForm;
    modalRef.componentInstance.id = 1;

    if (this.widgetComponent !== undefined) {
      this.widgetComponent.getCurrentWidgetConfig().subscribe(result => {
        modalRef.componentInstance.widgetConfig = result;
      });
      // Take form data, combine with widget config, and pass to update function
      modalRef.result.then((newConfig) => {
        if (!newConfig) {
          return;
        }
        this.widgetComponent.stopRefreshInterval();
        this.updateWidgetConfig(newConfig);
      }).catch((error) => {
      });
    }
  }

  updateWidgetConfig(newWidgetConfig: any): void {
    if (!newWidgetConfig) {
      return;
    }

    // Take the current config and prepare it for saving
    const newWidgetConfig$ = this.widgetComponent.getCurrentWidgetConfig().pipe(
      map( widgetConfig => {
        extend(widgetConfig, newWidgetConfig);
        return widgetConfig;
      }),
      map((widgetConfig: any) => {
        if (widgetConfig.collectorItemId) {
          widgetConfig.collectorItemIds = [widgetConfig.collectorItemId];
          delete widgetConfig.collectorItemId;
        }
        return widgetConfig;
      })
    );

    // Take the modified widgetConfig and upsert it.
    const upsertDashboardResult$ = newWidgetConfig$.pipe(
      switchMap(widgetConfig => {
        return this.dashboardService.upsertWidget(widgetConfig);
      }));

    // Take the new widget and the results from the API call
    // and have the dashboard service take this data to
    // publish the new config.
    zip(newWidgetConfig$, upsertDashboardResult$).pipe(
      map(([widgetConfig, upsertWidgetResponse]) => ({ widgetConfig, upsertWidgetResponse }))
    ).subscribe((result: IWidgetConfigResponse) => {
      if (result.widgetConfig !== null && typeof result.widgetConfig === 'object') {
        extend(result.widgetConfig, result.upsertWidgetResponse.widget);
      }

      this.dashboardService.upsertLocally(result.upsertWidgetResponse.component, result.widgetConfig);

      // Push the new config to the widget, which
      // will trigger whatever is subscribed to
      // widgetConfig$
      this.widgetComponent.widgetConfigSubject.next(result.widgetConfig);
      this.widgetComponent.startRefreshInterval();
    });
  }

  openConfirm() {
    const modalRef = this.modalService.open(ConfirmationModalComponent);
    modalRef.componentInstance.title = 'Are you sure want to delete this widget from your dashboard?';
    modalRef.componentInstance.modalType = ConfirmationModalComponent;
  }

  openAudit() {
    const modalRef = this.modalService.open(AuditModalComponent);
    modalRef.componentInstance.auditResult = this.auditResult;
  }

  private detectChanges(): void {
    const destroyed = 'destroyed';
    if (!this.cdr[destroyed]) {
      this.cdr.detectChanges();
    }
  }

  private findWidgetAuditStatus(auditType: string) {
    this.dashboardService.dashboardAuditConfig$.pipe(map(result => result))
      .subscribe((auditResults: IAuditResult[]) => {
        this.auditResult = auditResults.find(auditResult => auditResult.auditType === auditType);
        this.auditStatus = this.auditResult ? this.auditResult.auditStatus : '';
    });
  }
}

