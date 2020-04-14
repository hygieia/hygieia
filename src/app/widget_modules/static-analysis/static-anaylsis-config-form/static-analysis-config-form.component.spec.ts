import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { StaticAnalysisConfigFormComponent } from './static-analysis-config-form.component';

describe('StaticAnalysisConfigFormComponent', () => {
  let component: StaticAnalysisConfigFormComponent;
  let fixture: ComponentFixture<StaticAnalysisConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaticAnalysisConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getStaticAnalysisTitle', () => {
    const collectorItem = {
      description : 'example-repo',
      niceName : 'example',
    };
    expect(component.getStaticAnalysisTitle(collectorItem)).toEqual('example : example-repo');
    expect(component.getStaticAnalysisTitle(null)).toEqual('');
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 123,
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should have an initial static config form', () => {
    const widgetConfigData = {
      options: {
        id: 123,
      }
    };
    component.widgetConfig = widgetConfigData;
    expect(component.staticAnalysisConfigForm).toBeDefined();
  });

});
