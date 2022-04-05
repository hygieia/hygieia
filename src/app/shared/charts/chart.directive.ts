import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appChart]'
})
export class ChartDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
