import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecurityScanDetailComponent } from './security-scan-detail.component';

describe('SecurityScanDetailComponent', () => {
  let component: SecurityScanDetailComponent;
  let fixture: ComponentFixture<SecurityScanDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecurityScanDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityScanDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
