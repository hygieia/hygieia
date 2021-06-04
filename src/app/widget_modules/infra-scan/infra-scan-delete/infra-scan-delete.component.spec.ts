import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanDeleteComponent } from './infra-scan-delete.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {InfraScanModule} from '../infra-scan.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('InfraScanDeleteComponent', () => {
  let component: InfraScanDeleteComponent;
  let fixture: ComponentFixture<InfraScanDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ InfraScanModule, HttpClientTestingModule ],
      declarations: [],
      providers: [ NgbActiveModal ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
