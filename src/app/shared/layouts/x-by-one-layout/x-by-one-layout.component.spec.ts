import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { XByOneLayoutComponent } from './x-by-one-layout.component';

describe('XByOneLayoutComponent', () => {
  let component: XByOneLayoutComponent;
  let fixture: ComponentFixture<XByOneLayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ XByOneLayoutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(XByOneLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
