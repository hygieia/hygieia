import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDashboardComponent } from './admin-dashboard.component';
import { GenerateApiTokensComponent } from './generate-api-tokens/generate-api-tokens.component';
import { ManageAdminsComponent } from './manage-admins/manage-admins.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminFilterPipe } from '../../pipes/filter.pipe';
import { DashEditComponent } from './dash-edit/dash-edit.component';
import { DashTrashComponent } from './dash-trash/dash-trash.component';
import { AdminOrderByPipe } from '../../pipes/order-by.pipe';
import {FeatureFlagsComponent} from './feature-flags/feature-flags.component';

describe('AdminDashboardComponent', () => {
    let component: AdminDashboardComponent;
    let fixture: ComponentFixture<AdminDashboardComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [AdminDashboardComponent, GenerateApiTokensComponent, ManageAdminsComponent,
                 AdminOrderByPipe, AdminFilterPipe, DashTrashComponent, DashEditComponent, FeatureFlagsComponent],
            imports: [FormsModule, CommonModule, ReactiveFormsModule]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AdminDashboardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
