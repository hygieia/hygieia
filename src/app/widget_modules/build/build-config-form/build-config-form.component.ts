import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-build-config-form',
  templateUrl: './build-config-form.component.html',
  styleUrls: ['./build-config-form.component.scss']
})
export class BuildConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;

  buildConfigForm: FormGroup;
  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getBuildTitle = (collectorItem: any) => {
    if (!collectorItem) {
      return '';
    }
    const description = (collectorItem.description as string);
    return collectorItem.niceName + ' : ' + description;
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.buildConfigForm.get('buildDurationThreshold').setValue(widgetConfig.options.buildDurationThreshold);
    this.buildConfigForm.get('consecutiveFailureThreshold').setValue(widgetConfig.options.consecutiveFailureThreshold);
  }

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private collectorService: CollectorService,
    private dashboardService: DashboardService
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
            this.collectorService.searchItems('build', term).pipe(
              tap(() => this.searchFailed = false),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );

    this.loadSavedBuildJob();
    this.getDashboardComponent();
  }

  private createForm() {
    this.buildConfigForm = this.formBuilder.group({
      buildDurationThreshold: ['', Validators.required],
      consecutiveFailureThreshold: '',
      buildJob: ''
    });
  }

  private submitForm() {
    const newConfig = {
      name: 'build',
      options: {
        id: this.widgetConfigId,
        buildDurationThreshold: +this.buildConfigForm.value.buildDurationThreshold,
        consecutiveFailureThreshold: +this.buildConfigForm.value.consecutiveFailureThreshold
      },
      componentId: this.componentId,
      collectorItemId: this.buildConfigForm.value.buildJob.id
    };
    this.activeModal.close(newConfig);
  }

  private loadSavedBuildJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const buildCollector = dashboard.application.components[0].collectorItems.Build;
        const savedCollectorBuildJob = buildCollector ? buildCollector[0].description : null;

        if (savedCollectorBuildJob) {
          const buildId = buildCollector[0].id;
          return buildId;
        }
        return null;
      }),
      switchMap(buildId => {
        if (buildId) {
          return this.collectorService.getItemsById(buildId);
        }
        return of(null);
      })).subscribe(collectorData => {
        this.buildConfigForm.get('buildJob').setValue(collectorData);
      });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}
