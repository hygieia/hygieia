import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type, ViewChild} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {FormModalDirective} from './form-modal.directive';

@Component({
  selector: 'app-form-modal',
  templateUrl: './form-modal.component.html',
  styleUrls: ['./form-modal.component.scss']
})
export class FormModalComponent implements OnInit {

  @Input() title = 'Test';
  @Input() form: Type<any>;
  @Input() widgetConfig: Type<any>;
  @ViewChild(FormModalDirective, {static: true}) modalTypeTag: FormModalDirective;

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
    (componentRef.instance as FormModalComponent).widgetConfig = this.widgetConfig;
    this.cdr.detectChanges();
  }
  onSubmit() {
    this.activeModal.close();
  }
  closeModal() {
    this.activeModal.close('Modal Closed');
  }

}

