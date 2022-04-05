import { TestBed } from '@angular/core/testing';

import { InfraScanService } from './infra-scan.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('InfraScanService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [ HttpClientTestingModule ]
  }));

  it('should be created', () => {
    const service: InfraScanService = TestBed.get(InfraScanService);
    expect(service).toBeTruthy();
  });
});
