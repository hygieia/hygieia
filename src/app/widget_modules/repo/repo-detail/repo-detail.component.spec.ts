import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RepoDetailComponent } from './repo-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('RepoDetailComponent', () => {
  let component: RepoDetailComponent;
  let fixture: ComponentFixture<RepoDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepoDetailComponent ],
      providers: [ NgbActiveModal ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
