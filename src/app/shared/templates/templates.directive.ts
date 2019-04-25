import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appTemplates]'
})
export class TemplatesDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
