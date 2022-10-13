import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';
import { NfrrService } from './nfrr.service';

describe('NfrrService', () => {
    let service: NfrrService;
    let http: HttpClient;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule ]
        });
    }));

    beforeEach(() => {
        service = TestBed.inject(NfrrService);
        http = TestBed.inject(HttpClient);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should get all audit metrics', () => {
        const spy = spyOn(service, 'getTotalElementsCount').and.returnValue(of(0));
        service.getAuditMetricsAll();
        expect(spy).toHaveBeenCalled();
    });

    it('should get audit metrics by lob', () => {
        const spy = spyOn(service, 'getTotalElementsCount').and.returnValue(of(0));
        service.getAuditMetricsByLob('foo');
        expect(spy).toHaveBeenCalled();
    });

    it('should get total elements count', () => {
        const spy = spyOn(http, 'get').and.returnValue(of({}));
        service.getTotalElementsCount('foo');
        expect(spy).toHaveBeenCalled();
    });
});
