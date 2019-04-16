import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, ViewChild } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {DetailModalComponent} from '../detail-modal/detail-modal.component';
import {ModalDirective} from '../modal.directive';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss']
})
export class ConfirmationModalComponent implements OnInit {

  @Input() message = 'Would you like to confirm?';
  @Input() form = DetailModalComponent;
  // @ViewChild(ModalDirective) modalTypeTag: ModalDirective;

  constructor(
    public activeModal: NgbActiveModal, private componentFactoryResolver: ComponentFactoryResolver
  ) { }

  ngOnInit() {
    // let componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.form);
    //
    // let viewContainerRef = this.modalTypeTag.viewContainerRef;
    // viewContainerRef.clear();
    //
    // let componentRef = viewContainerRef.createComponent(componentFactory);
  }


}
