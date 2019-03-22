import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ComboSeriesVerticalComponent } from './combo-series-vertical.component';

describe('ComboSeriesVerticalComponent', () => {
  let component: ComboSeriesVerticalComponent;
  let fixture: ComponentFixture<ComboSeriesVerticalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ComboSeriesVerticalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComboSeriesVerticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
