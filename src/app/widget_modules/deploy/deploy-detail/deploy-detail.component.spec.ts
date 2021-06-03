import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeployDetailComponent } from './deploy-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('DeployDetailComponent', () => {
  let component: DeployDetailComponent;
  let fixture: ComponentFixture<DeployDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeployDetailComponent ],
      providers: [ NgbActiveModal ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeployDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
