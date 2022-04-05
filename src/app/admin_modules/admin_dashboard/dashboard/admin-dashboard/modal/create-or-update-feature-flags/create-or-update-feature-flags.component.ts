import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';

@Component({
  selector: 'app-create-or-update-feature-flags',
  templateUrl: './create-or-update-feature-flags.component.html',
  styleUrls: ['./create-or-update-feature-flags.component.scss']
})
export class CreateOrUpdateFeatureFlagsComponent implements OnInit {
  @Input() public name: string;
  @Input() public description: string;
  @Input() public flags = {
    agileTool: '',
    artifact: '',
    build: '',
    codeQuality: '',
    deployment: '',
    libraryPolicy: '',
    scm: '',
    staticSecurityScan: '',
    test: '',
  };

  featureFlagForm: FormGroup;
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
      this.featureFlagForm.get('name').setValue(this.name);
      this.featureFlagForm.get('description').setValue(this.description);
      for (const [currFlag, value] of Object.entries(this.flags)) {
        if (value) {
          this.featureFlagForm.get(currFlag).setValue(true);
        } else {
          this.featureFlagForm.get(currFlag).setValue(false);
        }
      }
      if (this.name) {
        this.disableName = true;
      }
    });
  }

  private createForm() {
    this.featureFlagForm = this.formBuilder.group({
      name: '',
      description: '',
      agileTool: Boolean,
      artifact: Boolean,
      build: Boolean,
      codeQuality: Boolean,
      deployment: Boolean,
      libraryPolicy: Boolean,
      scm: Boolean,
      staticSecurityScan: Boolean,
      test: Boolean
    });
  }

  get f() { return this.featureFlagForm.controls; }

  submit() {
    let featureFlagUpdateObj = {};
    if (this.featureFlagForm.valid) {
      const flags = {
        agileTool: this.featureFlagForm.get('agileTool').value,
        artifact: this.featureFlagForm.get('artifact').value,
        build: this.featureFlagForm.get('build').value,
        codeQuality: this.featureFlagForm.get('codeQuality').value,
        deployment: this.featureFlagForm.get('deployment').value,
        libraryPolicy: this.featureFlagForm.get('libraryPolicy').value,
        scm: this.featureFlagForm.get('scm').value,
        staticSecurityScan: this.featureFlagForm.get('staticSecurityScan').value,
        test: this.featureFlagForm.get('test').value
      };

      if (this.id) {
        // Edit
        featureFlagUpdateObj = {
          id : this.id,
          name : this.featureFlagForm.get('name').value,
          description : this.featureFlagForm.get('description').value,
          flags
        };
      } else {
        // Post
        featureFlagUpdateObj = {
          name: this.featureFlagForm.get('name').value,
          description: this.featureFlagForm.get('description').value,
          flags
        };
      }
      this.userData
        .createOrUpdateFeatureFlags(JSON.stringify(featureFlagUpdateObj))
        .subscribe( (response) => {
          this.activeModal.close('close');
        }, (error) => {
          console.log(error);
        });
    }
  }
}
