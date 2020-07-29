import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';

@Component({
  selector: 'app-create-or-update-service-account',
  templateUrl: './create-or-update-service-account.component.html',
  styleUrls: ['./create-or-update-service-account.component.scss']
})
export class CreateOrUpdateServiceAccountComponent implements OnInit {
  @Input() public serviceAccountName: string;
  @Input() public fileNames: string;

  serviceAccountForm: FormGroup;
  id: string;
  disableName = false;

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private userData: UserDataService
  ) {
    this.createForm();
  }

  ngOnInit() {
    setTimeout(() => {
      this.serviceAccountForm.get('serviceAccountName').setValue(this.serviceAccountName);
      this.serviceAccountForm.get('fileNames').setValue(this.fileNames);
    });
    if (this.serviceAccountName) {
      this.disableName = true;
    }
  }

  private createForm() {
    this.serviceAccountForm = this.formBuilder.group({
      serviceAccountName: '',
      fileNames: ''
    });
  }

  submit() {
    if (this.serviceAccountForm.valid) {
      const accountObj = {
        serviceAccount: this.serviceAccountForm.get('serviceAccountName').value,
        fileNames: this.serviceAccountForm.get('fileNames').value
      };
      if (this.id) {
        // Edit
        this.userData
          .updateAccount(accountObj, this.id)
          .subscribe( (response) => {
            this.activeModal.close('close');
          }, (error) => {
            console.log(error);
          });
      } else {
        // Post
        this.userData
          .createAccount(accountObj)
          .subscribe( (response) => {
            this.activeModal.close('close');
          }, (error) => {
            console.log(error);
          });
      }
    }
  }
}
