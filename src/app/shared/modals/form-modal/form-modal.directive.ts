import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appFormModal]'
})
export class FormModalDirective {

  constructor(public viewContainerRef: ViewContainerRef) {
  }

}
