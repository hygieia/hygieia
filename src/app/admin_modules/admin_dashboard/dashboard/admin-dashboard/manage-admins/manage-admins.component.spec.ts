import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { ManageAdminsComponent } from './manage-admins.component';
import { UserDataService } from '../../../../../shared/services/user-data.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {AdminFilterPipe} from '../../../../../shared/pipes/filter.pipe';
import {AdminOrderByPipe} from '../../../../../shared/pipes/order-by.pipe';

describe('ManageAdminsComponent', () => {
  let component: ManageAdminsComponent;
  let fixture: ComponentFixture<ManageAdminsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ManageAdminsComponent, AdminFilterPipe, AdminOrderByPipe],
      providers: [UserDataService],
      imports: [FormsModule, CommonModule, ReactiveFormsModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageAdminsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
