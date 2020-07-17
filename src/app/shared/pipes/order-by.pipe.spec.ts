import { GeneralOrderByPipe } from './order-by.pipe';
import { API_TOKEN_LIST } from '../../admin_modules/admin_dashboard/services/user-data.service.mockdata';

describe('OrderByPipe', () => {
  it('create an instance', () => {
    const pipe = new GeneralOrderByPipe();
    expect(pipe).toBeTruthy();
  });

  it('should order by pipe the apiuser', () => {
    expect(new GeneralOrderByPipe().transform(API_TOKEN_LIST, 'apiUser')).toBeTruthy();
  });

});
