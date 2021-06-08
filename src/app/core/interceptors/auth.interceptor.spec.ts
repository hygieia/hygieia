import { HttpHeaders, HTTP_INTERCEPTORS } from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { Observable, of } from "rxjs";
import { IUserLogin } from "src/app/shared/interfaces";
import { AuthService } from "../services/auth.service"
import { AuthInterceptor } from "./auth.interceptor";

class mockAuthService {
    register(userLogin: IUserLogin): Observable<boolean> {
        return of(true);
    }
}

describe('AuthInterceptor', () => {
    let service: AuthService;
    let http: HttpTestingController;
    
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [ 
                { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
            ]
        });
        service = TestBed.get(AuthService);
        http = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
        http.verify();
    });

    it('should add appropriate auth token to requests', () => {
        service.getAuthenticationProviders().subscribe(res => {
            expect(res).toBeTruthy();
        })
        const httpReq = http.expectOne('/api/authenticationProviders');

        expect(httpReq.request.headers.has('Authorization')).toEqual(true);
    })
})