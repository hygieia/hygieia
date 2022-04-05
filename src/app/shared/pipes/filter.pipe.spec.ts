import { GeneralFilterPipe } from './filter.pipe';
import { API_TOKEN_LIST, USER_LIST } from '../../admin_modules/admin_dashboard/services/user-data.service.mockdata';

describe('FilterPipe', () => {
  it('create an instance', () => {
    const pipe = new GeneralFilterPipe();
    expect(pipe).toBeTruthy();
  });

  it('should search the apiuser', () => {
    expect(new GeneralFilterPipe().transform(API_TOKEN_LIST, { apiUser: 'testing1' })).toBeTruthy();
  });

  it('should search the apiuser', () => {
    expect(new GeneralFilterPipe().transform(['testing', 'testing1'], 'testing1')).toBeTruthy();
  });

  it('should search the user list', () => {
    expect(new GeneralFilterPipe().transform(USER_LIST, { username: 'test', authorities: '!ROLE_ADMIN' })).toBeTruthy();
  });

});
