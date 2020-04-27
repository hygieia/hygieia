import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TwoByOneLayoutComponent } from './two-by-one-layout.component';

describe('TwoByOneLayoutComponent', () => {
  let component: TwoByOneLayoutComponent;
  let fixture: ComponentFixture<TwoByOneLayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TwoByOneLayoutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TwoByOneLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
