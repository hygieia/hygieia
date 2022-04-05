import { Directive, TemplateRef } from '@angular/core';

@Directive({
  selector: '[appIfTabActive]'
})
export class IfTabActiveDirective {
  constructor(public templateRef: TemplateRef<any>) {}
}
