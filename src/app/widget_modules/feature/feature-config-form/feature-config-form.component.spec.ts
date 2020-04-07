import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { FeatureConfigFormComponent } from './feature-config-form.component';

describe('FeatureConfigFormComponent', () => {
  let component: FeatureConfigFormComponent;
  let fixture: ComponentFixture<FeatureConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getProjectName and getTeamName', () => {
    const collectorItem = {
      options: {
        id: 123,
        featureTool: 'featureTool',
        sprintType: 'sprint',
        listType: 'listType',
        projectName: 'project',
        teamName: 'team'
      }
    };
    component.getProjectName(collectorItem);
    component.getProjectName(null);

    component.getTeamName(collectorItem);
    component.getTeamName(null);
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 123,
        featureTool: 'featureTool',
        sprintType: 'sprint',
        listType: 'listType',
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });
});
