import {HttpClientTestingModule} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import { AuthService } from './auth.service';
import {IUserLogin} from '../../shared/interfaces';
import {HttpRequest} from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let token: string;
  let userLogin: IUserLogin;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService],
      imports: [ HttpClientTestingModule]
    });
  });
  beforeEach(() => {
    token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC' +
    'J9.eyJzdWIiOiJqb2huRG9lIiwibmFtZSI6IkpvaG4g' +
      'RG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE3MTYyMzkwMjJ9.Ab4-2LjNKuJciqFfeTy5shRajqlOg91BgJijckd5cck';
    service = TestBed.get(AuthService);
    localStorage.setItem('access_token', token);
    localStorage.setItem('auth-code', 'test-code');
    userLogin = {
      password: 'passTest',
      username: 'userTest',
    };
  });
  afterEach(() => {
    localStorage.clear();
  });
  it('should get fake token', () => {
    expect(service.getToken()).toBe(token);
    expect(service.getAuthCode()).toBe('test-code');
  });
  it('should get users name', () => {
    expect(service.getUserName()).toBe('johnDoe');
  });
  it('should get expiration', () => {
    expect(service.getExpiration()).toBe(1716239022);
  });
  it('should token to be cleared', () => {
    service.logout();
    expect(service.getToken()).toBe(null);
    expect(service.getAuthCode()).toBe(null);
  });
  it('should be authenticated', () => {
    expect(service.isAuthenticated()).toBe(true);
  });
  it('should login', () => {
    service.loginLdap(userLogin);
    service.login(userLogin);
  });
  it('should check admin and authType', () => {
    service.isAdmin();
    service.getAuthType();
  });
  it('should register login', () => {
    service.register(userLogin);
  });
  it('should check sso login',  () =>  {
    expect(service.isSsoLogin(new HttpRequest('GET', '/api/login/openid'))).toBe(true);
  });
});
