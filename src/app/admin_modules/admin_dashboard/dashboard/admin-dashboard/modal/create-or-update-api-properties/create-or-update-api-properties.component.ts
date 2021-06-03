import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';

@Component({
  selector: 'app-create-or-update-api-properties',
  templateUrl: './create-or-update-api-properties.component.html',
  styleUrls: ['./create-or-update-api-properties.component.scss']
})
export class CreateOrUpdateApiPropertiesComponent implements OnInit {
  @Input() public name: string;
  @Input() public properties: {};
  propertiesJSON = '';
  apiPropertiesForm: FormGroup;
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
      this.apiPropertiesForm.get('name').setValue(this.name);
      this.apiPropertiesForm.get('properties').setValue(this.properties);
      this.propertiesJSON = JSON.stringify(this.properties);
      if (this.name) {
        this.disableName = true;
      }
    });
  }

  private createForm() {
    this.apiPropertiesForm = this.formBuilder.group({
      name: '',
      properties: ''
    });
  }

  submit() {
    let collector = {};
    if (this.id) {
      // Edit
      try {
        collector = {
          id: this.id,
          name: this.apiPropertiesForm.get('name').value,
          collectorType: 'Api',
          properties: JSON.parse(this.apiPropertiesForm.get('properties').value),
        };
      } catch (e) {
      }
    } else {
      // Post
      try {
        collector = {
          name: this.apiPropertiesForm.get('name').value,
          collectorType: 'Api',
          properties: JSON.parse(this.apiPropertiesForm.get('properties').value),
        };
      } catch (e) {
      }
    }
    this.userData
      .createOrUpdatePropertiesBuilder(collector)
      .subscribe( (response) => {
        this.activeModal.close('close');
        }, (error) => {
        console.log(error);
      });
  }
}
