import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-static-analysis-config-form',
  templateUrl: './static-analysis-config-form.component.html',
  styleUrls: ['./static-analysis-config-form.component.scss']
})
export class StaticAnalysisConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;

  staticAnalysisConfigForm: FormGroup;
  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getStaticAnalysisTitle = (collectorItem: any) => {
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
    this.staticAnalysisConfigForm.get('staticAnalysisJob').setValue(widgetConfig.collectorItemId);
  }

  constructor(
    public activeModal: NgbActiveModal,
    public formBuilder: FormBuilder,
    public collectorService: CollectorService,
    public dashboardService: DashboardService,
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
            this.collectorService.searchItems('codequality', term).pipe(
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

    this.loadSavedCodeQualityJob();
    this.getDashboardComponent();
  }

  public createForm() {
    this.staticAnalysisConfigForm = this.formBuilder.group({
      staticAnalysisJob: ['', Validators.required]
    });
  }

  public submitForm() {
    const newConfig = {
      name: 'codeanalysis',
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'codeanalysis0',
      },
      componentId: this.componentId,
      collectorItemId: this.staticAnalysisConfigForm.value.staticAnalysisJob.id
    };
    this.activeModal.close(newConfig);
  }

  public loadSavedCodeQualityJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const sonarCollector = dashboard.application.components[0].collectorItems.CodeQuality;
        const savedCollectorCodeQuality = sonarCollector ? sonarCollector[0].description : null;

        if (savedCollectorCodeQuality) {
          const codeQualityId = sonarCollector[0].id;
          return codeQualityId;
        }
        return null;
      }),
      switchMap(codeQualityId => {
        if (codeQualityId) {
          return this.collectorService.getItemsById(codeQualityId);
        }
        return of(null);
      })).subscribe(collectorData => {
        this.staticAnalysisConfigForm.get('staticAnalysisJob').setValue(collectorData);
      });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

  // convenience getter for easy access to form fields
  get configForm() { return this.staticAnalysisConfigForm.controls; }

}
