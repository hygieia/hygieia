import { Component, OnInit, Input } from '@angular/core';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { take, map, debounceTime, distinctUntilChanged, tap, switchMap, catchError } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { FormGroup, FormBuilder, FormArray, FormControl } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CollectorService } from 'src/app/shared/collector.service';
import { TestType } from '../interfaces';

@Component({
  selector: 'app-test-config-form',
  templateUrl: './test-config-form.component.html',
  styleUrls: ['./test-config-form.component.scss']
})
export class TestConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;
  readonly COLLECTOR_ITEM_TYPE = 'Test';

  testConfigForm: FormGroup;
  searchingFunctional = false;
  searchingPerformance = false;
  searchFunctionalFailed = false;
  searchPerformanceFailed = false;
  typeAheadResultsPerformance: (text$: Observable<string>) => Observable<any>;
  typeAheadResultsFunctional: (text$: Observable<string>) => Observable<any>;

  // Format test result title
  getTestResultTitle(collectorItem: any) {
    return collectorItem ? collectorItem.niceName + ' : ' + collectorItem.description : '';
  }

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) { return ; }
    this.widgetConfigId = widgetConfig.options.id;
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

    this.loadSavedTestResults();
    this.getDashboardComponent();

    this.typeAheadResultsPerformance = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searchingPerformance = true),
        switchMap(term => {
          return term.length < 1 ? of([]) :
            this.collectorService.searchItems(this.COLLECTOR_ITEM_TYPE, term).pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchPerformanceFailed = true;
                  return of([]);
                }
                this.searchPerformanceFailed = false;
              }),
              catchError(() => {
                this.searchPerformanceFailed = true;
                return of([]);
              })
            );
        }),
        tap(() => this.searchingPerformance = false),
      );

    this.typeAheadResultsFunctional = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searchingFunctional = true),
        switchMap(term => {
          return term.length < 1 ? of([]) :
            this.collectorService.searchItems(this.COLLECTOR_ITEM_TYPE, term).pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchFunctionalFailed = true;
                  return of([]);
                }
                this.searchFunctionalFailed = false;
              }),
              catchError(() => {
                this.searchFunctionalFailed = true;
                return of([]);
              })
            );
        }),
        tap(() => this.searchingFunctional = false),
      );
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

  // Create forms for each test collector item and load them into respective form arrays
  private loadSavedTestResults() {
    this.dashboardService.dashboardConfig$.pipe(take(1)).subscribe(dashboard => {
      const testCollectorItems = dashboard.application.components[0].collectorItems.Test;
      if (!testCollectorItems) { return; }
      let functionalTestCount = 0;
      let performanceTestCount = 0;
      for (const testCollectorItem of testCollectorItems) {
        if (testCollectorItem.options.testType === TestType.Functional.toString()) {
          this.addFunctionalTest();
          this.functionalTests.controls[functionalTestCount].get('test').setValue(testCollectorItem);
          functionalTestCount++;
        } else if (testCollectorItem.options.testType === TestType.Performance.toString()) {
          this.addPerformanceTest();
          this.performanceTests.controls[performanceTestCount].get('test').setValue(testCollectorItem);
          performanceTestCount++;
        }
      }
    });
  }

  private createForm() {
    this.testConfigForm = this.formBuilder.group({
      functionalTests: this.formBuilder.array([]),
      performanceTests: this.formBuilder.array([]),
    });
  }

  get functionalTests(): FormArray {
    return this.testConfigForm.get('functionalTests') as FormArray;
  }
  get performanceTests(): FormArray {
    return this.testConfigForm.get('performanceTests') as FormArray;
  }

  addFunctionalTest() {
    const test = this.formBuilder.group({
      test: new FormControl(''),
    });
    this.functionalTests.push(test);
  }

  addPerformanceTest() {
    const test = this.formBuilder.group({
      test: new FormControl(''),
    });
    this.performanceTests.push(test);
  }

  deleteFunctionalTest(i) {
    this.functionalTests.removeAt(i);
  }

  deletePerformanceTest(i) {
    this.performanceTests.removeAt(i);
  }


  // Create new config which will be posted to database
  submitForm() {
    const newConfig = {
      name: 'codeanalysis',
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'codeanalysis0'
      },
      componentId: this.componentId,
      collectorItemIds : []
    };
    for (const control of this.functionalTests.controls) {
      if (control.value.test.id !== undefined ) {
        newConfig.collectorItemIds.push(control.value.test.id);
      }
    }
    for (const control of this.performanceTests.controls) {
      if (control.value.test.id !== undefined ) {
        newConfig.collectorItemIds.push(control.value.test.id);
      }
    }
    this.activeModal.close(newConfig);
  }

  // convenience getter for easy access to form fields
  get configForm() { return this.testConfigForm.controls; }
}
