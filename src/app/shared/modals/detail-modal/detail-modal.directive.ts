import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appDetailModal]'
})
export class DetailModalDirective {

  constructor(public viewContainerRef: ViewContainerRef) {
  }

}
