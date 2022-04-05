import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CollectorService } from '../../../shared/collector.service';
import { DashboardService } from '../../../shared/dashboard.service';
import { catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';

@Component({
  selector: 'app-infra-scan-config',
  templateUrl: './infra-scan-config.component.html',
  styleUrls: ['./infra-scan-config.component.sass']
})
export class InfraScanConfigComponent implements OnInit {
  private componentId: string;
  private widgetConfigId: string;
  infraScanConfigForm: FormGroup;

  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getInfraScanJobTitle = (collectorItem: any) => {
    if (!collectorItem) {
      return '';
    }
    const businessComponent = (collectorItem.businessComponent as string);
    return collectorItem.collector.name + ' : ' + businessComponent;
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.infraScanConfigForm.get('iJob').setValue(widgetConfig.options.iJob);
  }

  constructor(
    public activeModal: NgbActiveModal,
    public formBuilder: FormBuilder,
    public collectorService: CollectorService,
    public dashboardService: DashboardService
  ) {
    this.createForm();
  }

  createForm() {
    this.infraScanConfigForm = this.formBuilder.group({
      iJob: ''
    });
  }

  ngOnInit() {

    this.typeAheadResults = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItems('InfrastructureScan', term).pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchFailed = true;
                  return of([]);
                }
                this.searchFailed = false;
              }),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );

    this.loadSavedInfraScanJob();
    this.getDashboardComponent();
  }

  submitForm() {
    const newConfig = {
      name: 'infrascan',
      componentId: this.componentId,
      collectorItemId: this.infraScanConfigForm.value.iJob.id,
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'infrascan0',
      },
    };
    this.activeModal.close(newConfig);
  }

  loadSavedInfraScanJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const infraScanCollector = dashboard.application.components[0].collectorItems.InfrastructureScan;
        const savedCollectorinfraScanJob = infraScanCollector ? infraScanCollector : null;

        if (savedCollectorinfraScanJob) {
          const securityId = savedCollectorinfraScanJob[0].id;
          return securityId;
        }
        return null;
      }),
      switchMap(securityId => {
        if (securityId) {
          return this.collectorService.getItemsById(securityId);
        }
        return of(null);
      })).subscribe(collectorData => {
        if (collectorData) {
          this.infraScanConfigForm.get('iJob').setValue(collectorData);
        }
      });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

  get configForm() { return this.infraScanConfigForm.controls; }

}
