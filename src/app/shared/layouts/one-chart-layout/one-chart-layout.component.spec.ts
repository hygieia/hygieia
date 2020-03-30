import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { OneChartLayoutComponent } from './one-chart-layout.component';

describe('OneChartLayoutComponent', () => {
  let component: OneChartLayoutComponent;
  let fixture: ComponentFixture<OneChartLayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [OneChartLayoutComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OneChartLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
