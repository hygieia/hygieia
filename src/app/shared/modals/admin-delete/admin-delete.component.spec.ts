import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDeleteComponent } from './admin-delete.component';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';

describe('AdminDeleteComponent', () => {
  let component: AdminDeleteComponent;
  let fixture: ComponentFixture<AdminDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminDeleteComponent ],
      providers: [NgbActiveModal],
      imports: [NgbModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
