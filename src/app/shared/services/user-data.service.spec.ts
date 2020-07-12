import { TestBed, inject } from '@angular/core/testing';
import { UserDataService } from './user-data.service';
import { HttpClientModule } from '@angular/common/http';
import { API_TOKEN_LIST, USER_LIST } from './user-data.service.mockdata';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

describe('UserDataService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, HttpClientModule],
    providers: [UserDataService]
  }));



  it('should be created',
    inject(
      [HttpTestingController, UserDataService],
      (
        httpMock: HttpTestingController,
        userDataService: UserDataService
      ) => {
        expect(userDataService).toBeTruthy();
      }
    )
  );

  it('should be Get Api Token',
    inject(
      [HttpTestingController, UserDataService],
      (
        httpMock: HttpTestingController,
        userDataService: UserDataService
      ) => {
        userDataService.apitokens().subscribe((data: any) => {
          expect(data).toBeTruthy();
          expect(data.length).toBe(2);
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(API_TOKEN_LIST);

      }
    )
  );

  it('should be Get user List',
    inject(
      [HttpTestingController, UserDataService],
      (
        httpMock: HttpTestingController,
        userDataService: UserDataService
      ) => {
        userDataService.apitokens().subscribe((data: any) => {
          expect(data).toBeTruthy();
          expect(data.length).toBe(2);
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(USER_LIST);

      }
    )
  );


  it('should be able to update token',
    inject([HttpTestingController, UserDataService],
      (httpMock: HttpTestingController, service: UserDataService) => {

        service.updateToken(API_TOKEN_LIST[0], '12345678901234567890').subscribe(result => {
          expect(result).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'POST');
        request.flush(API_TOKEN_LIST[0]);
      })
  );

  it('should be able to create token',
    inject([HttpTestingController, UserDataService],
      (httpMock: HttpTestingController, service: UserDataService) => {

        service.createToken(API_TOKEN_LIST[0]).subscribe(result => {
          expect(result).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'POST');
        request.flush(API_TOKEN_LIST[0]);
      })
  );

  it('should be able to delete token',
    inject([HttpTestingController, UserDataService],
      (httpMock: HttpTestingController, service: UserDataService) => {

        service.deleteToken(API_TOKEN_LIST[0].id).subscribe(result => {
          expect(result).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'DELETE');
        request.flush(API_TOKEN_LIST[0].id);
      })
  );

});
