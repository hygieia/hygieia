import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectorRefreshComponent } from './collector-refresh.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {SharedModule} from '../../../../shared/shared.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CollectorItemModule} from '../../collector-item.module';
import {RouterTestingModule} from '@angular/router/testing';

describe('CollectorRefreshComponent', () => {
  let component: CollectorRefreshComponent;
  let fixture: ComponentFixture<CollectorRefreshComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule,
        RouterTestingModule.withRoutes([]), CollectorItemModule
      ],
      declarations: [],
      providers: [NgbActiveModal]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectorRefreshComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should get JSON HTML', () => {
    const mockData = {
        id: 'dummyID',
        template: 'CapOne',
        title: 'Hygieia',
        type: 'Team'
      };
    component.getJsonHtml(mockData);
  });

});
