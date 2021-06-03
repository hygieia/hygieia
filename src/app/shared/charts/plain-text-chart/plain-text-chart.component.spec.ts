import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlainTextChartComponent } from './plain-text-chart.component';

describe('PlainTextChartComponent', () => {
  let component: PlainTextChartComponent;
  let fixture: ComponentFixture<PlainTextChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlainTextChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlainTextChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should output the title and data', () => {
    component.title = 'Title';
    component.data = 'Data';
    fixture.detectChanges();
    const plainTextChartElement: HTMLElement = fixture.nativeElement;
    expect(plainTextChartElement.textContent).toContain('Title');
    expect(plainTextChartElement.textContent).toContain('Data');
  });
});
