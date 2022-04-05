import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseEvidenceComponent } from './base-evidence.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {SharedModule} from '../../../../shared/shared.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CollectorItemModule} from '../../collector-item.module';
import {CollectorItemService} from '../../collector-item.service';
import {ActivatedRoute} from '@angular/router';
import {ICollItem} from '../../interfaces';
import {RouterTestingModule} from '@angular/router/testing';

describe('BaseEvidenceComponent', () => {
  let component: BaseEvidenceComponent;
  let fixture: ComponentFixture<BaseEvidenceComponent>;
  let ciTestData: ICollItem;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule,
        RouterTestingModule.withRoutes([]), CollectorItemModule
      ],
      declarations: [],
      providers: [NgbModal, CollectorItemService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get(): string {
                  return '123';
                }
              },
              url: [
                {path: 'url1'},
                {path: 'url2'},
                {path: 'url3'}
              ]
            },
          },
        }]
    }).compileComponents();
    ciTestData = {
      id: 'testID',
      description: 'desc',
      niceName: 'niceNm',
      environment: 'env',
      enabled: true,
      pushed: false,
      collectorId: 'collID',
      lastUpdated: 1555590574399,
      options: {
        dashboardId: 'dashID',
        jobName: 'jobNm',
        jobUrl: 'jobURL'
      },
      errorCount: 0,
      errors: []
    } as ICollItem;
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BaseEvidenceComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit() and ngAfterViewInit()', () => {
    component.ngOnInit();
    expect(component.collector).toEqual('url1');
    component.ngAfterViewInit();
  });
  it('should call ngOnDestroy()', () => {
    component.ngOnDestroy();
  });

  it('should open Details', () => {
    component.openDetails(ciTestData);
  });

  it( 'should apply filter', () => {
    component.ngOnInit();
    component.applyFilter('filterString');
    expect(component.dataSource.filter).toEqual('filterstring');
  });
});
