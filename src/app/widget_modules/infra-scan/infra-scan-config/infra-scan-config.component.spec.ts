import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanConfigComponent } from './infra-scan-config.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {InfraScanModule} from '../infra-scan.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('InfraScanConfigComponent', () => {
  let component: InfraScanConfigComponent;
  let fixture: ComponentFixture<InfraScanConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ InfraScanModule, HttpClientTestingModule ],
      declarations: [],
      providers: [ NgbActiveModal ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
