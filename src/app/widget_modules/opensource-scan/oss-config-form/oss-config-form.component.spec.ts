import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OSSConfigFormComponent } from './oss-config-form.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {SharedModule} from '../../../shared/shared.module';
import {ReactiveFormsModule} from '@angular/forms';

describe('OSSConfigFormComponent', () => {
  let component: OSSConfigFormComponent;
  let fixture: ComponentFixture<OSSConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OSSConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOssTitle', () => {
    const collectorItem = {
      description : 'example-oss',
      niceName : 'example',
    };
    expect(component.getOssTitle(collectorItem)).toEqual('example-oss');
    expect(component.getOssTitle(null)).toEqual('');
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 788,
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should getOssTitle', () => {
    const collectorItem = {
      description : 'example-oss'
    };
    expect(component.getOssTitle(collectorItem)).toEqual('example-oss');
    expect(component.getOssTitle(null)).toEqual('');
  });

});
