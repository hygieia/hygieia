import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {Component, NgModule} from '@angular/core';
import {By} from '@angular/platform-browser';

import { DashboardViewComponent } from './dashboard-view.component';
import {SharedModule} from '../../../shared/shared.module';
import {CaponeTemplateComponent} from '../capone-template/capone-template.component';

@Component({
  selector: 'app-test-widget',
  template: ''

})
class TestWidgetComponent {}


@NgModule({
  declarations: [TestWidgetComponent],
  entryComponents: [
    TestWidgetComponent
  ]
})
class TestModule { }

describe('DashboardViewComponent', () => {
  let component: DashboardViewComponent;
  let fixture: ComponentFixture<DashboardViewComponent>;



  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [  DashboardViewComponent ],
      imports: [TestModule, HttpClientTestingModule, SharedModule, CommonModule, RouterModule.forRoot([])]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardViewComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});



