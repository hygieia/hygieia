import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanConfigComponent } from './infra-scan-config.component';

describe('InfraScanConfigComponent', () => {
  let component: InfraScanConfigComponent;
  let fixture: ComponentFixture<InfraScanConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InfraScanConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
