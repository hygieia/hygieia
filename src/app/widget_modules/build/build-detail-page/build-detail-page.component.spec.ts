import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BuildDetailPageComponent } from './build-detail-page.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('BuildDetailComponent', () => {
  let component: BuildDetailPageComponent;
  let fixture: ComponentFixture<BuildDetailPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildDetailPageComponent ],
      providers: [ NgbActiveModal ]
    })
    .compileComponents();
  }));
});
