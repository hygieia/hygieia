import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {GeneralDashboardDeleteComponent} from './general-dashboard-delete.component';

describe('GeneralDashboardDeleteComponent', () => {
  let component: GeneralDashboardDeleteComponent;
  let fixture: ComponentFixture<GeneralDashboardDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralDashboardDeleteComponent ],
      providers: [NgbActiveModal],
      imports: [NgbModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralDashboardDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
