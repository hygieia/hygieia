import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type, ViewChild} from '@angular/core';
import {NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {ConfirmationModalDirective} from './confirmation-modal.directive';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss']
})
export class ConfirmationModalComponent implements OnInit {

  @Input() message = 'Would you like to confirm?';
  @Input() form: Type<any>;
  @Input() widgetConfig: Type<any>;
  @ViewChild(ConfirmationModalDirective, { static: true }) modalTypeTag: ConfirmationModalDirective;
  @Input() title: any;

  constructor(
    public activeModal: NgbActiveModal,
    private componentFactoryResolver: ComponentFactoryResolver,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.form);
    const viewContainerRef = this.modalTypeTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as ConfirmationModalComponent).widgetConfig = this.widgetConfig;
    this.cdr.detectChanges();
  }


}
