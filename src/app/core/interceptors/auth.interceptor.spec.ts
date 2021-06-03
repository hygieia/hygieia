import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { AuthService } from "../services/auth.service"
import { AuthInterceptor } from "./auth.interceptor";

describe('AuthInterceptor', () => {
    let service: AuthService;
    
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ],
            providers: [ 
                AuthService,
                { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
            ]
        });
        service = TestBed.get(AuthService);
    });

    it('should add appropriate auth token to requests', () => {
        spyOn(service, "getToken").and.returnValue("foobar");
        
    })
})