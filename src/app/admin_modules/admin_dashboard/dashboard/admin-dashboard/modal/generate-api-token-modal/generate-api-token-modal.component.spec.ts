import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateApiTokenModalComponent } from './generate-api-token-modal.component';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('GenerateApiTokenModalComponent', () => {
  let component: GenerateApiTokenModalComponent;
  let fixture: ComponentFixture<GenerateApiTokenModalComponent>;


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GenerateApiTokenModalComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, HttpClientTestingModule]

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateApiTokenModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a  generate token from ', () => {
    component.ngOnInit();
    expect(component.apiForm).toBeTruthy();
  });

  it('should  submit generate from  when clicked on submit', () => {
    component.apiForm.get('apiUser').setValue('testing1');
    component.submit();
    expect(component.apiForm.valid).toBeTruthy();
  });

});
