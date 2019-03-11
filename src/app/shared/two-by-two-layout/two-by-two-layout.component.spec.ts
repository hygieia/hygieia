import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TwoByTwoLayoutComponent } from './two-by-two-layout.component';

describe('TwoByTwoLayoutComponent', () => {
  let component: TwoByTwoLayoutComponent;
  let fixture: ComponentFixture<TwoByTwoLayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TwoByTwoLayoutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TwoByTwoLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
