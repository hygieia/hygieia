import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RepoDeleteFormComponent } from './repo-delete-form.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {Observable, of} from 'rxjs';
import {CollectorService} from '../../../shared/collector.service';
import {NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {SharedModule} from '../../../shared/shared.module';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {RepoModule} from '../repo.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'Github : repo1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'Github',
      collectorType: 'SCM'
    }
  };

  getItemsById(id: string): Observable<any> {
    return of(this.mockCollectorData);
  }
}

class MockDashboardService {
  mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          SCM: [{
            id: '1234',
            description: 'Github : repo1'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule, RepoModule],
  entryComponents: []
})
class TestModule { }

describe('RepoDeleteFormComponent', () => {
  let component: RepoDeleteFormComponent;
  let fixture: ComponentFixture<RepoDeleteFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  /*const scmCollectorItem = {
    id: '1234',
    description: 'repo1',
    collectorId: '4321',
    collector: {
      id: '4321',
      name: 'Github',
      collectorType: 'SCM'
    }
  };*/

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, HttpClientTestingModule, ReactiveFormsModule, NgbModule],
      declarations: [],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoDeleteFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    collectorService = TestBed.get(CollectorService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set widgetConfig', () => {
    let widgetConfigData = {
      options: {
        id: 1234,
        url: '',
      }
    };
    component.widgetConfig = widgetConfigData;

    widgetConfigData = null;
    component.widgetConfig = widgetConfigData;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should assign selected job after submit', () => {
    component.createDeleteForm();
    expect(component.repoDeleteForm.get('url').value).toEqual('');
    component.repoDeleteForm = component.formBuilder.group({url: 'github.com/repo'});
    component.submitDeleteForm();
    expect(component.repoDeleteForm.get('url').value).toEqual('github.com/repo');
  });

});
