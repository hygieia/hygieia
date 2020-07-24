import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {GeneralDeleteComponent} from '../../../../../shared/modals/general-delete-modal/general-delete-modal.component';
import {CreateOrUpdateApiPropertiesComponent} from '../modal/create-or-update-api-properties/create-or-update-api-properties.component';

@Component({
  selector: 'app-properties-builder',
  templateUrl: './properties-builder.component.html',
  styleUrls: ['./properties-builder.component.scss']
})
export class PropertiesBuilderComponent implements OnInit {

  error: any = {};
  apiProperties: any = {};
  constructor(private userData: UserDataService, private modalService: NgbModal) { }
  apiPropertiesSearch = '';
  name: string;

  ngOnInit() {
    this.loadProperties();
  }

  loadProperties() {
    this.userData.getApiPropertiesBuilderData('Api').subscribe((response: any) => {
      this.apiProperties = response;
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

  deleteProperties(id) {
    const modalRef = this.modalService.open(GeneralDeleteComponent);
    modalRef.componentInstance.title = 'Are you sure you want to delete?';
    modalRef.result.then((newConfig) => {
      this.userData.deleteProperties(id).subscribe(response => {
        this.loadProperties();
      });
    });
  }
}
