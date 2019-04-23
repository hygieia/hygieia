import { TestBed } from '@angular/core/testing';

import { DashboardViewService } from './dashboard-view.service';

describe('DashboardViewService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DashboardViewService = TestBed.get(DashboardViewService);
    expect(service).toBeTruthy();
  });
});
