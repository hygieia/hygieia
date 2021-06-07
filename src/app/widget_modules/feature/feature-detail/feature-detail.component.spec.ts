import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FeatureDetailComponent } from './feature-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('FeatureDetailComponent', () => {
  let component: FeatureDetailComponent;
  let fixture: ComponentFixture<FeatureDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FeatureDetailComponent ],
      providers: [ NgbActiveModal ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
