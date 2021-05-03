import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RefreshModalComponent } from './refresh-modal.component';

import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatIconModule} from '@angular/material/icon';

describe('SecurityScanRefreshModalComponent', () => {
  let component: RefreshModalComponent;
  let fixture: ComponentFixture<RefreshModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RefreshModalComponent ],
      providers: [ NgbActiveModal ],
      imports: [NgbModule, MatIconModule ]

    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RefreshModalComponent);
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
  });
});
