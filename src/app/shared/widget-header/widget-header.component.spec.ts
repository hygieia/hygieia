import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WidgetHeaderComponent } from './widget-header.component';
import {
  Component,
  ComponentFactoryResolver,
  NO_ERRORS_SCHEMA, ViewContainerRef,
} from '@angular/core';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {HttpClientModule} from '@angular/common/http';
import {FormModalComponent} from '../modals/form-modal/form-modal.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {WidgetDirective} from '../widget/widget.directive';
import {ConfirmationModalComponent} from '../modals/confirmation-modal/confirmation-modal.component';

describe('WidgetHeaderComponent', () => {
  let component: WidgetHeaderComponent;
  let fixture: ComponentFixture<WidgetHeaderComponent>;
  let componentFactoryResolver: ComponentFactoryResolver;

  @Component({
    template: ''
  })

  class TestWidgetTypeComponent {}

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WidgetHeaderComponent, TestWidgetTypeComponent ],
      imports: [ HttpClientModule, NgbModule ],
      schemas: [ NO_ERRORS_SCHEMA ],
    });
    // .compileComponents();

    TestBed.overrideModule(BrowserDynamicTestingModule, {
      set: {
        declarations: [FormModalComponent, WidgetDirective, ConfirmationModalComponent ],
        entryComponents: [ TestWidgetTypeComponent, FormModalComponent, ConfirmationModalComponent ],
      }
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetHeaderComponent);
    component = fixture.componentInstance;
    component.widgetType = TestWidgetTypeComponent;
    componentFactoryResolver = fixture.debugElement.injector.get(ComponentFactoryResolver);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should updateWidgetConfig', () => {
    component.loadComponent();
    component.updateWidgetConfig(null);
  });

  it('should openConfig when clicked', () => {
    component.openConfig();
  });

  it('should have an initial widget value when loading', () => {
    component.ngOnInit();
    expect(component.widgetType).toBeDefined();
  });

  it('should open the delete confirmation when delete icon is clicked', () => {
    component.openConfirm();
  });
});
