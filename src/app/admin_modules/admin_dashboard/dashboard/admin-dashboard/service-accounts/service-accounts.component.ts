import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {CreateOrUpdateServiceAccountComponent} from '../modal/create-or-update-service-account/create-or-update-service-account.component';
import { AdminDeleteComponent } from '../modal/admin-delete/admin-delete.component';

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

  deleteServiceAccount(id) {
    const modalRef = this.modalService.open(AdminDeleteComponent);
    modalRef.componentInstance.title = 'Are you sure you want to delete?';
    modalRef.result.then((newConfig) => {
      this.userData.deleteServiceAccount(id).subscribe(response => {
        this.loadServiceAccounts();
      });
    });
  }
}
