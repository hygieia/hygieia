import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {GeneralDeleteComponent} from '../../../../../shared/modals/general-delete-modal/general-delete-modal.component';
// tslint:disable-next-line:max-line-length
import {CreateOrUpdateApiPropertiesComponent} from '../modal/create-or-update-api-properties/create-or-update-api-properties.component';
// tslint:disable-next-line:max-line-length
import {CreateOrUpdateApiAuditPropertiesComponent} from '../modal/create-or-update-api-audit-properties/create-or-update-api-audit-properties.component';

@Component({
  selector: 'app-properties-builder',
  templateUrl: './properties-builder.component.html',
  styleUrls: ['./properties-builder.component.scss']
})
export class PropertiesBuilderComponent implements OnInit {

  error: any = {};
  apiProperties: any;
  apiPropertiesSearch = '';
  apiAuditProperties: any;
  apiAuditPropertiesSearch = '';
  name: string;
  p = 1;
  panelExpandedApi = false;
  panelExpandedApiAudit = false;

  constructor(private userData: UserDataService, private modalService: NgbModal) {
  }

  ngOnInit() {
    this.loadProperties();
  }

  loadProperties() {
    this.userData.getPropertiesBuilderData('Api').subscribe((response: any) => {
      this.apiProperties = response;
    });
    this.userData.getPropertiesBuilderData('ApiAudit').subscribe((response: any) => {
      this.apiAuditProperties = response;
    });
  }

  stringifyObj(obj) {
    return JSON.stringify(obj);
  }

  properKeys(obj) {
    return Object.keys(obj);
  }

  addNewApiPropertiesBuilder() {
    const modalRef = this.modalService.open(CreateOrUpdateApiPropertiesComponent);
    modalRef.result.then((newConfig) => {
      this.loadProperties();
    });
  }

  editApiPropertiesBuilder(collector) {
    const modalRef = this.modalService.open(CreateOrUpdateApiPropertiesComponent);
    modalRef.componentInstance.id = collector.id;
    modalRef.componentInstance.name = collector.name;
    modalRef.componentInstance.properties = collector.properties;
    modalRef.result.then((newConfig) => {
      this.loadProperties();
    });
  }

  addNewApiAuditPropertiesBuilder() {
    const modalRef = this.modalService.open(CreateOrUpdateApiAuditPropertiesComponent);
    modalRef.result.then((newConfig) => {
      this.loadProperties();
    });
  }

  editApiAuditPropertiesBuilder(collector) {
    const modalRef = this.modalService.open(CreateOrUpdateApiAuditPropertiesComponent);
    modalRef.componentInstance.id = collector.id;
    modalRef.componentInstance.name = collector.name;
    modalRef.componentInstance.properties = collector.properties;
    modalRef.result.then((newConfig) => {
      this.loadProperties();
    });
  }

  deleteProperties(collector) {
    const modalRef = this.modalService.open(GeneralDeleteComponent);
    modalRef.componentInstance.title = collector.name;
    modalRef.result.then((newConfig) => {
      this.userData.deleteProperties(collector.id).subscribe(response => {
        this.loadProperties();
      });
    });
  }
}
