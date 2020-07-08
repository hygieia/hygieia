import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { TestConfigFormComponent } from './test-config-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import {TestModule} from '../test.module';

describe('TestConfigFormComponent', () => {
  let component: TestConfigFormComponent;
  let fixture: ComponentFixture<TestConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      providers: [NgbActiveModal]
    })

    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should set widget config', () => {
    const widgetConfigData = {
      options: {
        id: 'test0'
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should get test result title', () => {
    const collectorItem = {
      niceName: 'niceName',
      description: 'description',
      options: {
        id: 123,
        testType: 'Functional',
        jobName: 'job name',
        instanceUrl: 'instanceUrl'
      }
    };
    expect(component.getTestResultTitle(collectorItem)).toBe('niceName : description');
  });

  it('should get/add functional tests', () => {
    expect(component.functionalTests.controls.length).toBe(0);
    component.addFunctionalTest();
    expect(component.functionalTests.controls.length).toBe(1);
    component.addFunctionalTest();
    expect(component.functionalTests.controls.length).toBe(2);
  });

  it('should get/add performance tests', () => {
    expect(component.performanceTests.controls.length).toBe(0);
    component.addPerformanceTest();
    expect(component.performanceTests.controls.length).toBe(1);
    component.addPerformanceTest();
    expect(component.performanceTests.controls.length).toBe(2);
  });

});
