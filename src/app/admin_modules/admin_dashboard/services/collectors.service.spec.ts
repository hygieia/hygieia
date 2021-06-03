import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CollectorsService } from './collectors.service';

const mockCollector = [
    {
        id: '239871983',
        name: 'github',
        collectorType: 'foo',
        enabled: true,
        online: true,
        lastExecuted: 141312837
    },
    {
        id: '2312308921',
        name: 'jmeter',
        collectorType: 'bar',
        enabled: false,
        online: false,
        lastExecuted: 9876543
    }
];

describe('CollectorService', () => {
    let service: CollectorsService;
    let httpClient: HttpClient;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ HttpClientTestingModule, HttpClientModule],
            providers: [CollectorsService]
        });

        service = TestBed.get(CollectorsService);
        httpClient = TestBed.get(HttpClient);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should get colletors', () => {
        spyOn(httpClient, 'get').and.returnValue(of(mockCollector));
        service.getAllCollectors().subscribe((response) => {
            expect(response).toEqual(mockCollector);
        });
    });
});
