import {Component, Input, OnInit} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {Observable, of} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap} from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-feature-config-form',
  templateUrl: './feature-config-form.component.html',
  styleUrls: ['./feature-config-form.component.scss'],
})

export class FeatureConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;
  featureTool = [];
  estimateMetricType = [];
  sprintType = [];
  listType = [];
  public teamId: string;
  public projectId: string;

  submitted = false;
  featureConfigForm: FormGroup;
  searchingProject = false;
  searchingTeam = false;
  searchProjectFailed = false;
  searchTeamFailed = false;
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
        tap(() => this.searchingProject = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItemsBySearchField('AgileTool', term, 'options.projectName').pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchProjectFailed = true;
                  return of([]);
                }
                this.searchProjectFailed = false;
              }),
              catchError(() => {
                this.searchProjectFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searchingProject = false)
      );
    this.typeAheadResultsTeam = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searchingTeam = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItemsBySearchField('AgileTool', term, 'options.teamName').pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchTeamFailed = true;
                  return of([]);
                }
                this.searchTeamFailed = false;
              }),
              catchError(() => {
                this.searchTeamFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searchingTeam = false)
      );
    this.loadSavedFeatures();
    this.getDashboardComponent();
  }

  public createForm() {
    this.featureConfigForm = this.formBuilder.group({
      featureTool: ['', Validators.required],
      projectName: ['', Validators.required],
      teamName: ['', Validators.required],
      sprintType: ['', Validators.required],
      listType: ['', Validators.required],
      estimateMetricType: ['', Validators.required],
    });
    this.getAgileTools();
    this.estimateMetricType = this.getEstimateMetricTypes();
    this.listType = this.getListTypes();
    this.sprintType = this.getSprintTypes();
  }

  private getAgileTools() {
    this.collectorService.collectorsByType('AgileTool').subscribe(agileCollectors => {
      const featureTools = agileCollectors.map(currAgileTool => currAgileTool.name);
      const result = [];
      for (const currTool of featureTools) {
        result.push({type: currTool});
      }
      this.featureTool = result;
    });
  }

  public submitForm() {
    this.submitted = true;
    if (this.featureConfigForm.invalid) {
      return;
    }
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

  public getEstimateMetricTypes() {
    return [
      {type: 'hours', value: 'Hours'},
      {type: 'storypoints', value: 'Story Points' },
      {type: 'count', value: 'Issue Count' }];
  }

  public getListTypes() {
    return [{type: 'epics', value: 'Epics'}, {type: 'issues', value: 'Issues'}];
  }

  public getSprintTypes() {
    return [{type: 'scrum', value: 'Scrum'}, {type: 'kanban', value: 'Kanban'}, {type: 'scrumkanban', value: 'Both'}];
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

  // convenience getter for easy access to form fields
  get configForm() { return this.featureConfigForm.controls; }
}
