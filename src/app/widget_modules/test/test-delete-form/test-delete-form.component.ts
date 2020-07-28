import { Component, Input, OnInit } from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { map, take } from 'rxjs/operators';
import { DashboardService } from 'src/app/shared/dashboard.service';
import {TestType} from '../interfaces';

@Component({
  selector: 'app-test-delete-form',
  templateUrl: './test-delete-form.component.html',
  styleUrls: ['./test-delete-form.component.scss']
})
export class TestDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This test item will be deleted immediately. Would you like to delete?';

  private componentId: string;
  widgetConfigId: string;

  testDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
  }

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private dashboardService: DashboardService
  ) {
    this.createDeleteForm();
  }

  ngOnInit() {
    this.getSavedTestResults();
    this.getDashboardComponent();
  }

  private createDeleteForm() {
    this.testDeleteForm = this.formBuilder.group({
      functionalTests: this.formBuilder.array([]),
      performanceTests: this.formBuilder.array([]),
    });
  }

  public getSavedTestResults() {
    this.dashboardService.dashboardConfig$.pipe(take(1)).subscribe(dashboard => {
      const testCollectorItems = dashboard.application.components[0].collectorItems.Test;
      if (!testCollectorItems) {
        return;
      }
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

  submitDeleteForm() {
    const deleteConfig = {
      name: 'codeanalysis',
      options: {
        id: this.widgetConfigId
      },
      componentId: this.componentId,
      collectorItemIds : []
    };
    for (const control of this.functionalTests.controls) {
      if (control.value.test.id !== undefined ) {
        deleteConfig.collectorItemIds.push(control.value.test.id);
      }
    }
    for (const control of this.performanceTests.controls) {
      if (control.value.test.id !== undefined ) {
        deleteConfig.collectorItemIds.push(control.value.test.id);
      }
    }
    this.activeModal.close(deleteConfig);
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }

  get functionalTests(): FormArray {
    return this.testDeleteForm.get('functionalTests') as FormArray;
  }
  get performanceTests(): FormArray {
    return this.testDeleteForm.get('performanceTests') as FormArray;
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
}
