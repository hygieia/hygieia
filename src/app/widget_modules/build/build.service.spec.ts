import { TestBed } from '@angular/core/testing';

import { BuildService } from './build.service';

describe('BuildService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BuildService = TestBed.get(BuildService);
    expect(service).toBeTruthy();
  });
});
