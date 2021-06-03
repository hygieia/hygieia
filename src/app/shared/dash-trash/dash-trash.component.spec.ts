import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashTrashComponent } from './dash-trash.component';

describe('DashTrashComponent', () => {
  let component: DashTrashComponent;
  let fixture: ComponentFixture<DashTrashComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DashTrashComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashTrashComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
