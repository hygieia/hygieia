import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, NgModule } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../../../shared/shared.module';
import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { DashboardViewComponent } from './dashboard-view.component';

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
  it('should create template', () => {
    component.widgets = [{component: TestWidgetComponent,
                           status: 'Success'}];
    component.ngOnInit();
    component.ngAfterViewInit();
    const childDebugElement = fixture.debugElement.query(By.directive(CaponeTemplateComponent));
    expect(childDebugElement).toBeTruthy();
  });
});



