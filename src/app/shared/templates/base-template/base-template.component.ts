import { Component } from '@angular/core';

import { IWidget } from '../../interfaces';

@Component({
  template: '',
  styleUrls: ['./base-template.component.scss']
})
export class BaseTemplateComponent  {

  widgets: IWidget[];

  constructor() { }

}
