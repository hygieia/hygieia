import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// @ts-ignore
import moment from 'moment';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';

@Component({
  selector: 'app-edit-token-modal',
  templateUrl: './edit-token-modal.component.html',
  styleUrls: ['./edit-token-modal.component.scss']
})
export class EditTokenModalComponent implements OnInit {


  @Input() public apiUser = '';
  public tokenItem: any;
  @Input() public date: NgbDateStruct;
  apiEditForm: FormGroup;
  apiTokenError = false;


  constructor(public activeModal: NgbActiveModal, private formBuilder: FormBuilder, private userData: UserDataService) {

  }

  toDateModel(date: NgbDateStruct | null): string | null {
    return date ? date.month + '/' + date.day + '/' + date.year : null;
  }

  ngOnInit() {
    this.apiEditForm = this.formBuilder.group({
      apiUser: [{ value: '', disabled: true },
      [Validators.required, Validators.minLength(6), Validators.maxLength(50), Validators.pattern(/^[a-zA-Z0-9 ]*$/)]],
      date: [{ value: '' }]
    });
    setTimeout(() => {
      this.apiEditForm.get('apiUser').setValue(this.apiUser);
      this.apiEditForm.get('date').setValue(this.date);
    }, 100);
  }

  get f() { return this.apiEditForm.controls; }

  submit() {
    this.apiTokenError = true;
    if (this.apiEditForm.valid) {
      const id = this.tokenItem.id;
      const momentSelectedDt = moment(this.toDateModel(this.apiEditForm.get('date').value));
      const timemsendOfDay = momentSelectedDt.endOf('day').valueOf();

      const apitoken = {
        apiUser: this.apiEditForm.get('apiUser').value,
        expirationDt: timemsendOfDay
      };

      this.userData
        .updateToken(apitoken, id)
        .subscribe((response) => {
          this.apiTokenError = false;
          this.activeModal.close('close');
        }, (error) => {
          console.log(error);
        });
    }

  }
}
