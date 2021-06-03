import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanDetailComponent } from './infra-scan-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('InfraScanDetailComponent', () => {
  let component: InfraScanDetailComponent;
  let fixture: ComponentFixture<InfraScanDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InfraScanDetailComponent ],
      providers: [ NgbActiveModal ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
