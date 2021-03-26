import { TestBed } from '@angular/core/testing';

import { SsoService } from './sso.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('SsoService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [SsoService],
    imports: [ HttpClientTestingModule]
  }));

  it('should be created', () => {
    const service: SsoService = TestBed.get(SsoService);
    expect(service).toBeTruthy();
  });
});
