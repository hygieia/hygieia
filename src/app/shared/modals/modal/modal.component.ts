import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, ViewChild} from '@angular/core';
import { NgbModal, NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import {DetailModalComponent} from "../detail-modal/detail-modal.component";
import {ModalDirective} from "../modal.directive";
import {FormModalComponent} from "../form-modal/form-modal.component";
import {ConfirmationModalComponent} from "../confirmation-modal/confirmation-modal.component";

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss']
})
export class ModalComponent implements OnInit {

  @Input() title = 'Test';
  @Input() modalType = DetailModalComponent;
  @ViewChild(ModalDirective) modalTypeTag: ModalDirective;

  constructor(
    public activeModal: NgbActiveModal, private componentFactoryResolver: ComponentFactoryResolver
  ) { }

  ngOnInit() {
    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.modalType);

    let viewContainerRef = this.modalTypeTag.viewContainerRef;
    viewContainerRef.clear();

    let componentRef = viewContainerRef.createComponent(componentFactory);
    // (<AdComponent>componentRef.instance).data = adItem.data;
    // this.modalType = DetailModalComponent;
  }

}
