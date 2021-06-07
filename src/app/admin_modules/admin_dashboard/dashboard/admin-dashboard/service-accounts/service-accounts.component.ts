import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {CreateOrUpdateServiceAccountComponent} from '../modal/create-or-update-service-account/create-or-update-service-account.component';
import {GeneralDeleteComponent} from '../../../../../shared/modals/general-delete-modal/general-delete-modal.component';

@Component({
  selector: 'app-service-accounts',
  templateUrl: './service-accounts.component.html',
  styleUrls: ['./service-accounts.component.scss']
})
export class ServiceAccountsComponent implements OnInit {

  error: any = {};
  serviceAccounts: ServiceAccountsComponent[] = [];
  id: string;
  serviceAccountSearch = '';
  serviceAccountName: string;
  fileNames: string;
  p = 1;
  constructor(private userData: UserDataService, private modalService: NgbModal) { }

  ngOnInit() {
    this.loadServiceAccounts();
  }

  loadServiceAccounts() {
    this.userData.getServiceAccounts().subscribe((response: any) => {
      this.serviceAccounts = response;
    });
  }

  createAccount() {
    const modalRef = this.modalService.open(CreateOrUpdateServiceAccountComponent);
    modalRef.result.then((newConfig) => {
      this.loadServiceAccounts();
    });
  }

  updateAccount(serviceAccountObj) {
    const modalRef = this.modalService.open(CreateOrUpdateServiceAccountComponent);
    modalRef.componentInstance.id = serviceAccountObj.id;
    modalRef.componentInstance.serviceAccountName = serviceAccountObj.serviceAccountName;
    modalRef.componentInstance.fileNames = serviceAccountObj.fileNames;
    modalRef.result.then((newConfig) => {
      this.loadServiceAccounts();
    });
  }

  deleteServiceAccount(serviceAccount) {
    const modalRef = this.modalService.open(GeneralDeleteComponent);
    modalRef.componentInstance.title = serviceAccount.serviceAccountName;
    modalRef.result.then((newConfig) => {
      this.userData.deleteServiceAccount(serviceAccount.id).subscribe(response => {
        this.loadServiceAccounts();
      });
    });
  }
}
