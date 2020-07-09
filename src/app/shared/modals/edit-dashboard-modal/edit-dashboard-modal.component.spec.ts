import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { EditDashboardModalComponent } from './edit-dashboard-modal.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UserDataService } from 'src/app/shared/services/user-data.service';
import {AdminFilterPipe} from '../../pipes/filter.pipe';
import {AdminOrderByPipe} from '../../pipes/order-by.pipe';

describe('EditDashboardModalComponent', () => {
  let component: EditDashboardModalComponent;
  let fixture: ComponentFixture<EditDashboardModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditDashboardModalComponent, AdminFilterPipe, AdminOrderByPipe],
      providers: [NgbActiveModal, UserDataService],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDashboardModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
