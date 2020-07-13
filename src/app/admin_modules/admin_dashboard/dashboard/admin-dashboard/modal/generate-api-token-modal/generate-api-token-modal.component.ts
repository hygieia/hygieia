import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbDateStruct, NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';
// @ts-ignore
import moment from 'moment';


@Component({
  selector: 'app-generate-api-token-modal',
  templateUrl: './generate-api-token-modal.component.html',
  styleUrls: ['./generate-api-token-modal.component.scss']
})
export class GenerateApiTokenModalComponent implements OnInit {

  public apiUser: any;
  public tokenItem: any;
  public date: NgbDateStruct;
  apiForm: FormGroup;
  apiTokenError = false;

  constructor(public activeModal: NgbActiveModal, private formBuilder: FormBuilder,
              private userData: UserDataService, private calendar: NgbCalendar) { }

  ngOnInit() {
    this.apiForm = this.formBuilder.group({
      apiUser: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(50), Validators.pattern(/^[a-zA-Z0-9 ]*$/)]],
      date: [this.calendar.getToday()],
      apiKey: [{ value: '', disabled: true }, [Validators.maxLength(100)]]
    });
  }
  get f() { return this.apiForm.controls; }

  toDateModel(date: NgbDateStruct | null): string | null {
    return date ? date.month + '/' + date.day + '/' + date.year : null;
  }

  submit() {
    this.apiTokenError = true;
    if (this.apiForm.valid) {
      const momentSelectedDt = moment(this.toDateModel(this.apiForm.get('date').value));
      const timemsendOfDay = momentSelectedDt.endOf('day').valueOf();
      const apitoken = {
        apiUser: this.apiForm.get('apiUser').value,
        expirationDt: timemsendOfDay
      };
      this.userData
        .createToken(apitoken)
        .subscribe((response) => {
          this.apiForm.get('apiKey').setValue(response);
          this.apiTokenError = false;
        }, (error) => {
          this.apiTokenError = true;
        });
    }
  }

}
