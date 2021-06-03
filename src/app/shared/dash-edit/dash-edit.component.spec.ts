import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashEditComponent } from './dash-edit.component';

describe('DashEditComponent', () => {
  let component: DashEditComponent;
  let fixture: ComponentFixture<DashEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DashEditComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
