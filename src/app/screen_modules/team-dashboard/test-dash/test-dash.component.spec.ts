import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestDashComponent } from './test-dash.component';
import {WidgetHeaderComponent} from '../../../shared/widget-header/widget-header.component';
import {BuildWidgetComponent} from '../../../widget_modules/build/build-widget/build-widget.component';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';

describe('TestDashComponent', () => {
  let component: TestDashComponent;
  let fixture: ComponentFixture<TestDashComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestDashComponent, WidgetHeaderComponent, BuildWidgetComponent ]
    });
    fixture = TestBed.createComponent(TestDashComponent);
    component = fixture.componentInstance;
  }));
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
