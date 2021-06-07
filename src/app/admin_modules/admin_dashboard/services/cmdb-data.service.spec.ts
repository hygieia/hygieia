import { TestBed } from '@angular/core/testing';

import { CmdbDataService } from './cmdb-data.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';

describe('CmdbDataService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, HttpClientModule],
    providers: [CmdbDataService]
  }));

  it('should be created', () => {
    const service: CmdbDataService = TestBed.get(CmdbDataService);
    expect(service).toBeTruthy();
  });
});
