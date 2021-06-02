import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestDetailComponent } from './test-detail.component';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {TestModule} from '../test.module';

describe('TestDetailComponent', () => {
  let component: TestDetailComponent;
  let fixture: ComponentFixture<TestDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      providers: [NgbActiveModal]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should set detailData', () => {
    const detailData = {
      title: 'title',
      subtitle: [],
      data: {
        id: '1',
        collectorItemId: '1',
        timestamp: 1556890677839,
        executionId: '1',
        description: 'Success',
        url: 'xxx.com',
        startTime: 1556889630000,
        endTime: 1556890537000,
        duration: 907000,
        failureCount: 0,
        successCount: 1,
        skippedCount: 0,
        totalCount: 1,
        unknownStatusCount: 0,
        type: 'Performance',
        resultStatus: 'Success',
        testCapabilities: []
      }
    };
    component.detailData = detailData;
    expect(component.data).toEqual(detailData.data);
  });

  it('should return false if theres no data', () => {
    expect(component.data).toBeFalsy();
  });

});

