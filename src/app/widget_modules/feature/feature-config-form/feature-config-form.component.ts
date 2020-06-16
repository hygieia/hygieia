import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-feature-config-form',
  templateUrl: './feature-config-form.component.html',
  styleUrls: ['./feature-config-form.component.scss']
})
export class FeatureConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;
  public teamId: string;
  public projectId: string;

  featureConfigForm: FormGroup;
  searching = false;
  searchFailed = false;
  typeAheadResultsProject: (text$: Observable<string>) => Observable<any>;
  typeAheadResultsTeam: (text$: Observable<string>) => Observable<any>;

  getProjectName = (collectorItem: any) => {
    if (!collectorItem) {
      return '';
    }
    const projectName = (collectorItem.options.projectName as string);
    return projectName;
  }

  getTeamName = (collectorItem) => {
    if (!collectorItem) {
      return '';
    }
    const teamName = (collectorItem.options.teamName as string);
    return teamName;
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.featureConfigForm.get('featureTool').setValue(widgetConfig.options.featureTool);
    this.featureConfigForm.get('sprintType').setValue(widgetConfig.options.sprintType);
    this.featureConfigForm.get('listType').setValue(widgetConfig.options.listType);
    this.featureConfigForm.get('estimateMetricType').setValue(widgetConfig.options.estimateMetricType);
  }

  constructor(
    public activeModal: NgbActiveModal,
    public formBuilder: FormBuilder,
    private collectorService: CollectorService,
    private dashboardService: DashboardService
  ) {
    this.createForm();
  }

  ngOnInit() {
    this.typeAheadResultsProject = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItemsBySearchField('AgileTool', term, 'options.projectName').pipe(
              tap(() => this.searchFailed = false),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );
    this.typeAheadResultsTeam = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItemsBySearchField('AgileTool', term, 'options.teamName').pipe(
              tap(() => this.searchFailed = false),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );
    this.loadSavedFeatures();
    this.getDashboardComponent();
  }

  public createForm() {
    this.featureConfigForm = this.formBuilder.group({
      featureTool: '',
      projectName: '',
      teamName: '',
      sprintType: '',
      listType: '',
      estimateMetricType: '',
    });
  }

  public submitForm() {
    const newConfig = {
      name: 'feature',
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'feature0',
        featureTool: this.featureConfigForm.value.featureTool,
        teamName: this.featureConfigForm.value.teamName.options.teamName,
        teamId: this.teamId,
        projectName: this.featureConfigForm.value.projectName.options.projectName,
        projectId: this.projectId,
        estimateMetricType: this.featureConfigForm.value.estimateMetricType,
        sprintType: this.featureConfigForm.value.sprintType,
        listType: this.featureConfigForm.value.listType,
      },
      componentId: this.componentId,
      collectorItemId: this.featureConfigForm.value.projectName.id
    };
    this.activeModal.close(newConfig);
  }

  public loadSavedFeatures() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const featureCollector = dashboard.application.components[0].collectorItems.AgileTool;

        if (featureCollector[0].id) {
          const featureId = featureCollector[0].id;
          return featureId;
        }
        return null;
      }),
      switchMap(featureId => {
        if (featureId) {
          return this.collectorService.getItemsById(featureId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.teamId = collectorData.options.teamId;
      this.projectId = collectorData.options.projectId;
      this.featureConfigForm.get('projectName').setValue(collectorData);
      this.featureConfigForm.get('teamName').setValue(collectorData);
    });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}
