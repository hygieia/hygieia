import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetHeaderComponent } from './widget-header.component';
import {findComponentView} from '@angular/core/src/render3/util';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {BuildWidgetComponent} from '../../widget_modules/build/build-widget/build-widget.component';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';

describe('WidgetHeaderComponent', () => {
  let component: WidgetHeaderComponent;
  let fixture: ComponentFixture<WidgetHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WidgetHeaderComponent, BuildWidgetComponent ],
      imports: [  ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
    });
    // .compileComponents();

    TestBed.overrideModule(BrowserDynamicTestingModule, {
        set: {
          entryComponents: [ BuildWidgetComponent ]
        }
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetHeaderComponent);
    component = fixture.componentInstance;
    component.widgetType = BuildWidgetComponent;
  });

  it('should have a widget value', () => {
    component.ngOnInit();

    expect(component.widgetType).toBeDefined();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
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
