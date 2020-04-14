import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import {RepoConfigFormComponent} from './repo-config-form.component';

describe('RepoConfigFormComponent', () => {
  let component: RepoConfigFormComponent;
  let fixture: ComponentFixture<RepoConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set widgetConfig', () => {
    let widgetConfigData = null;
    component.widgetConfig = widgetConfigData;

    widgetConfigData = {
      options: {
        id: 'testId',
        scm: 'testScm',
        url: 'testUrl',
        branch: 'testBranch',
        userID: 'testUser',
        password: 'testPass',
        personalAccessToken: 'testPersonalAccess',
      }
    };
    component.widgetConfig = widgetConfigData;
  });

  it('should getDashboardComponents', () => {
    component.ngOnInit();
  });

});
