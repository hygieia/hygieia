import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteConfirmModalComponent } from '../../../../../shared/modals/delete-confirm-modal/delete-confirm-modal.component';
import { CreateOrUpdateFeatureFlagsComponent } from '../modal/create-or-update-feature-flags/create-or-update-feature-flags.component';
import { UpdateJsonComponent } from '../modal/update-json/update-json.component';
import { ViewJsonComponent } from '../modal/view-json/view-json.component';

@Component({
  selector: 'app-feature-flags',
  templateUrl: './feature-flags.component.html',
  styleUrls: ['./feature-flags.component.scss']
})
export class FeatureFlagsComponent implements OnInit {

  error: any = {};
  featureFlags: FeatureFlagsComponent[] = [];
  id: string;
  name: string;
  description: string;
  flags: {
    artifact: boolean;
    libraryPolicy: boolean;
    agileTool: boolean;
    test: boolean;
    build: boolean;
    codeQuality: boolean;
    staticSecurityScan: boolean;
    scm: boolean;
    deployment: boolean };
  constructor(private userData: UserDataService, private modalService: NgbModal) { }

  ngOnInit() {
    this.loadFeatureFlags();
  }

  loadFeatureFlags() {
    this.userData.getFeatureFlagsData().subscribe((response: any) => {
      this.featureFlags = response;
    });
  }

  flagKeys(obj) {
    return Object.keys(obj);
  }

  exportJSON() {
    const modalRef = this.modalService.open(ViewJsonComponent);
    modalRef.componentInstance.featureFlags = JSON.stringify(this.featureFlags);
  }

  addFile() {
    const modalRef = this.modalService.open(UpdateJsonComponent);
    modalRef.result.then((newConfig) => {
        this.loadFeatureFlags();
    });
  }

  addNewFeatureFlag() {
    const modalRef = this.modalService.open(CreateOrUpdateFeatureFlagsComponent);
    modalRef.result.then((newConfig) => {
      this.loadFeatureFlags();
    });
  }

  editFeatureFlag(featureFlagObj) {
    const modalRef = this.modalService.open(CreateOrUpdateFeatureFlagsComponent);
    modalRef.componentInstance.id = featureFlagObj.id;
    modalRef.componentInstance.name = featureFlagObj.name;
    modalRef.componentInstance.description = featureFlagObj.description;
    modalRef.componentInstance.flags = featureFlagObj.flags;
    modalRef.result.then((newConfig) => {
        this.loadFeatureFlags();
    });
  }

  deleteFeatureFlag(id) {
    const modalRef = this.modalService.open(DeleteConfirmModalComponent);
    modalRef.componentInstance.title = 'Are you sure you want to delete?';
    modalRef.result.then((newConfig) => {
      this.userData.deleteFeatureFlags(id).subscribe(response => {
        this.loadFeatureFlags();
      });
    });
  }
}
