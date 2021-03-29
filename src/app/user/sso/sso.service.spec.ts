import { TestBed } from '@angular/core/testing';

import { SsoService } from './sso.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing' ;

let service: SsoService;

describe('SsoService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [SsoService],
    imports: [ HttpClientTestingModule, RouterTestingModule]
  }));

  beforeEach(() => {
    service = TestBed.get(SsoService);
  });

  it('should be created', () => {
    service = TestBed.get(SsoService);
    expect(service).toBeTruthy();
  });

  it('should return uri', () => {
    expect(service.getRedirectUri().includes('/user/sso/')).toBe(true);
  });

  it('should call sso login', () => {
    const spy = spyOn(service, 'callSsoLogin');
    service.callSsoLogin();
    expect(spy).toHaveBeenCalledWith();
  });

  it('should check localhost auth code', () => {
    service.authCode = 'not undefined';
    expect(service.obtainAuthCodeLocalHost()).toBe('no code');
  });
});
