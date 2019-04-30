import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlaceholderWidgetComponent } from './placeholder-widget.component';

describe('PlaceholderWidgetComponent', () => {
  let component: PlaceholderWidgetComponent;
  let fixture: ComponentFixture<PlaceholderWidgetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlaceholderWidgetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlaceholderWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
