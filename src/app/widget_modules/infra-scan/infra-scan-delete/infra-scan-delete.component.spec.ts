import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanDeleteComponent } from './infra-scan-delete.component';

describe('InfraScanDeleteComponent', () => {
  let component: InfraScanDeleteComponent;
  let fixture: ComponentFixture<InfraScanDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InfraScanDeleteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
