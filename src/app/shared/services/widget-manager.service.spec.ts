import { TestBed } from '@angular/core/testing';

import { WidgetManagerService } from './widget-manager.service';

const option = {
  view: {
      defaults: {
          title: 'test'
      },
      controller: 'testController',
      controllerAs: 'testView',
      templateUrl: 'components/widgets/deploy/test.html'
  },
  config: {
      controller: 'testController',
      controllerAs: 'testConfig',
      templateUrl: 'components/widgets/deploy/test.html'
  },
};

describe('WidgetManagerService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: WidgetManagerService = TestBed.get(WidgetManagerService);
    expect(service).toBeTruthy();
  });

  it('should be register created', () => {
    const service: WidgetManagerService = TestBed.get(WidgetManagerService);
    service.register('testWidget',  option );
    expect(service.widgets).toBeTruthy();
  });

  it('should be created', () => {
    const service: WidgetManagerService = TestBed.get(WidgetManagerService);
    service.register('testWidget1', option);

    const widgets = service.getWidgets();

    expect(widgets).toBeTruthy();
  });

  it('should be created', () => {
    const service: WidgetManagerService = TestBed.get(WidgetManagerService);
    service.register('testWidget2', option);

    const widgets = service.getWidget('testWidget2');

    expect(widgets).toBeTruthy();
  });

});
