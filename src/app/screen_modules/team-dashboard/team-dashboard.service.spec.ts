import { TestBed } from '@angular/core/testing';

import { TeamDashboardService } from './team-dashboard.service';

describe('TeamDashboardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TeamDashboardService = TestBed.get(TeamDashboardService);
    expect(service).toBeTruthy();
  });
});
