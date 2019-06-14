import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashStatusComponent } from './dash-status.component';

describe('DashStatusComponent', () => {
  let component: DashStatusComponent;
  let fixture: ComponentFixture<DashStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DashStatusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
