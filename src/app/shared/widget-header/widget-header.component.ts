import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type, ViewChild} from '@angular/core';
import {FormModalComponent} from '../modals/form-modal/form-modal.component';
import {TestFormComponent} from '../../widget_modules/build/test-form/test-form.component';
import {ConfirmationModalComponent} from '../modals/confirmation-modal/confirmation-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DetailModalComponent} from '../modals/detail-modal/detail-modal.component';
import {LayoutDirective} from '../layouts/layout.directive';
import {LayoutComponent} from '../layouts/layout/layout.component';
import {WidgetDirective} from '../widget/widget.directive';
import {BuildWidgetComponent} from '../../widget_modules/build/build-widget/build-widget.component';
import {BuildConfigFormComponent} from '../../widget_modules/build/build-config-form/build-config-form.component';

@Component({
  selector: 'app-widget-header',
  templateUrl: './widget-header.component.html',
  styleUrls: ['./widget-header.component.scss']
})
export class WidgetHeaderComponent implements OnInit {

  @Input() widgetType: Type<any>;
  @Input() title;
  @ViewChild(WidgetDirective) appWidget: WidgetDirective;

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef, private modalService: NgbModal) {
  }

  ngOnInit() {
    // this.widgetType = BuildWidgetComponent;
    // this.title = 'Build';
    this.loadComponent();
    console.log(this.widgetType);
  }

  loadComponent() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.widgetType);
    const viewContainerRef = this.appWidget.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    this.cdr.detectChanges();
  }

  openConfig() {
    const modalRef = this.modalService.open(FormModalComponent);
    modalRef.componentInstance.title = 'Configure';
    modalRef.componentInstance.form = BuildConfigFormComponent;
    modalRef.componentInstance.id = 1;
    modalRef.result.then((result) => {
      console.log(result);
    }).catch((error) => {
      console.log(error);
    });
  }

  openConfirm() {
    const modalRef = this.modalService.open(ConfirmationModalComponent);
    modalRef.componentInstance.title = 'Are you sure want to delete this widget from your dashboard?';
    // modalRef.componentInstance.modalType = ConfirmationModalComponent;
  }

  ngAfterInit() {

  }
}
