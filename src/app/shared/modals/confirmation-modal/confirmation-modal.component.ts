import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, ViewChild } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {DetailModalComponent} from '../detail-modal/detail-modal.component';
import {FormModalDirective} from '../form-modal/form-modal.directive';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss']
})
export class ConfirmationModalComponent implements OnInit {

  @Input() message = 'Would you like to confirm?';
  @Input() form = DetailModalComponent;
  // @ViewChild(FormModalDirective) modalTypeTag: FormModalDirective;

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
