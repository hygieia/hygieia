import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {CollectorService} from '../../../shared/collector.service';
import {DashboardService} from '../../../shared/dashboard.service';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap} from 'rxjs/operators';
import {Observable, of} from 'rxjs';

@Component({
  selector: 'app-security-scan-config',
  templateUrl: './security-scan-config.component.html',
  styleUrls: ['./security-scan-config.component.scss']
})
export class SecurityScanConfigComponent implements OnInit {
  private componentId: string;
  private widgetConfigId: string;
  securityConfigForm: FormGroup;

  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getSecurityJobTitle = (collectorItem: any) => {
    if (!collectorItem) {
      return '';
    }
    const description = (collectorItem.description as string);
    return collectorItem.collector.name + ' : ' + description;
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.securityConfigForm.get('sJob').setValue(widgetConfig.options.sJob);
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
    this.securityConfigForm = this.formBuilder.group({
      sJob: ''
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
            this.collectorService.searchItems('StaticSecurityScan', term).pipe(
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

    this.loadSavedSecurityJob();
    this.getDashboardComponent();
  }

  submitForm() {
    const newConfig = {
      name: 'codeanalysis',
      componentId: this.componentId,
      collectorItemId: this.securityConfigForm.value.sJob.id,
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'codeanalysis0',
      },
    };
    this.activeModal.close(newConfig);
  }

  loadSavedSecurityJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const securityCollector = dashboard.application.components[0].collectorItems.StaticSecurityScan;
        const savedCollectorSecurityJob = securityCollector ? securityCollector[0].description : null;

        if (savedCollectorSecurityJob) {
          const securityId = securityCollector[0].id;
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
        this.securityConfigForm.get('sJob').setValue(collectorData);
      }
    });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

  // convenience getter for easy access to form fields
  get configForm() { return this.securityConfigForm.controls; }
}
