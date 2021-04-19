import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecurityScanRefreshModalComponent } from './security-scan-refresh-modal.component';

import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatIconModule} from '@angular/material/icon';

describe('SecurityScanRefreshModalComponent', () => {
  let component: SecurityScanRefreshModalComponent;
  let fixture: ComponentFixture<SecurityScanRefreshModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecurityScanRefreshModalComponent ],
      providers: [ NgbActiveModal ],
      imports: [NgbModule, MatIconModule ]

    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityScanRefreshModalComponent);
    component = fixture.componentInstance;
    const title = 'Project 1';
    const message = 'Updated 2 components.';
    component.title = title;
    component.message = message;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set title', () => {
    expect(component.title).toEqual('Project 1');
  });

  it('should close', () => {
    component.closeModal();
  })
});
