import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appConfirmationModal]'
})
export class ConfirmationModalDirective {

  constructor(public viewContainerRef: ViewContainerRef) {
  }

}
