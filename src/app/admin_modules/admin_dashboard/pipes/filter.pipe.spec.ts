import { AdminFilterPipe } from './filter.pipe';
import { API_TOKEN_LIST, USER_LIST } from '../services/user-data.service.mockdata';

describe('FilterPipe', () => {
  it('create an instance', () => {
    const pipe = new AdminFilterPipe();
    expect(pipe).toBeTruthy();
  });

  it('should search the apiuser', () => {
    expect(new AdminFilterPipe().transform(API_TOKEN_LIST, { apiUser: 'testing1' })).toBeTruthy();
  });

  it('should search the apiuser', () => {
    expect(new AdminFilterPipe().transform(['testing', 'testing1'], 'testing1')).toBeTruthy();
  });

  it('should search the user list', () => {
    expect(new AdminFilterPipe().transform(USER_LIST, { username: 'test', authorities: '!ROLE_ADMIN' })).toBeTruthy();
  });

});
