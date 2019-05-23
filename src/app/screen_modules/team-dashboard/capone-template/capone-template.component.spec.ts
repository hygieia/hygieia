import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaponeTemplateComponent } from './capone-template.component';
import {PlaceholderWidgetComponent} from '../../../shared/widget/placeholder-widget/placeholder-widget.component';
import { WidgetHeaderComponent } from '../../../shared/widget-header/widget-header.component';
import {Test} from 'tslint';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {Component, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA} from '@angular/core';
import {BuildConfigFormComponent} from '../../../widget_modules/build/build-config-form/build-config-form.component';
import {HttpClient, HttpClientModule} from '@angular/common/http';

describe('CaponeTemplateComponent', () => {
  let component: CaponeTemplateComponent;
  let fixture: ComponentFixture<CaponeTemplateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CaponeTemplateComponent
      ],
      schemas: [ NO_ERRORS_SCHEMA ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaponeTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
