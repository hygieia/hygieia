import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetHeaderComponent } from './widget-header.component';
import {Component, NO_ERRORS_SCHEMA} from '@angular/core';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {HttpClientModule} from '@angular/common/http';

describe('WidgetHeaderComponent', () => {
  let component: WidgetHeaderComponent;
  let fixture: ComponentFixture<WidgetHeaderComponent>;

  @Component({
    template: ''
  })
  class TestWidgetTypeComponent {}

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WidgetHeaderComponent, TestWidgetTypeComponent],
      imports: [ HttpClientModule ],
      schemas: [ NO_ERRORS_SCHEMA ]
    });
    // .compileComponents();

    TestBed.overrideModule(BrowserDynamicTestingModule, {
        set: {
          entryComponents: [ TestWidgetTypeComponent ]
        }
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetHeaderComponent);
    component = fixture.componentInstance;
    component.widgetType = TestWidgetTypeComponent;
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  // it('should not have an initial widget value before loading', () => {
  //   component.ngOnInit();
  //
  //   expect(component.widgetType).toBeUndefined();
  // });
  // it('should load a component type in based on the specified widget', () => {
  //
  // });
  // it('should open the configuration form when clicked', () => {
  //
  // });
  // it('should open the delete confirmation when delete icon is clicked', () => {
  //
  // });
});
