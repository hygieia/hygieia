import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {GeneralDeleteComponent} from './general-delete-modal.component';

describe('GeneralDeleteComponent', () => {
  let component: GeneralDeleteComponent;
  let fixture: ComponentFixture<GeneralDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralDeleteComponent ],
      providers: [NgbActiveModal],
      imports: [NgbModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
