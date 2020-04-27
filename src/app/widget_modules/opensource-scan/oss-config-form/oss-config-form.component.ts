import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap} from 'rxjs/operators';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-oss-config-form',
  templateUrl: './oss-config-form.component.html',
  styleUrls: ['./oss-config-form.component.scss']
})
export class OSSConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;
  private dashboard: any;

  ossConfigForm: FormGroup;
  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getOssTitle = (collectorItem: any) => {
    if (!collectorItem) {
      return '';
    }
    const description = (collectorItem.description as string);
    return description;
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.ossConfigForm.get('ossJob').setValue(widgetConfig.collectorItemId);
  }

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private collectorService: CollectorService,
    private dashboardService: DashboardService,
  ) {
    this.createForm();
  }

  ngOnInit() {
    this.typeAheadResults = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItems('LibraryPolicy', term).pipe(
              tap(() => this.searchFailed = false),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );

    this.loadSavedOssJob();
    this.getDashboardComponent();
  }

  private createForm() {
    this.ossConfigForm = this.formBuilder.group({
      ossJob: ['', Validators.required]
    });
  }

  private submitForm() {
    const newConfig = {
      name: 'oss',
      options: {
        id: this.widgetConfigId,
      },
      componentId: this.componentId,
      collectorItemId: this.ossConfigForm.value.ossJob.id
    };
    this.activeModal.close(newConfig);
  }

  private loadSavedOssJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const ossCollector = dashboard.application.components[0].collectorItems.LibraryPolicy;
        const savedCollectorOSS = ossCollector ? ossCollector[0].description : null;

        if (savedCollectorOSS) {
          const ossId = ossCollector[0].id;
          return ossId;
        }
        return null;
      }),
      switchMap(ossId => {
        if (ossId) {
          return this.collectorService.getItemsById(ossId);
        }
        return of(null);
      })).subscribe(collectorData => {
        this.ossConfigForm.get('ossJob').setValue(collectorData);
    });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        this.dashboard = dashboard;
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

}
