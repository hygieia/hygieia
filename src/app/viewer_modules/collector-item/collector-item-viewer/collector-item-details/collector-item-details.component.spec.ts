import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectorItemDetailsComponent } from './collector-item-details.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {RouterTestingModule} from '@angular/router/testing';

describe('CollectorItemDetailsComponent', () => {
  let component: CollectorItemDetailsComponent;
  let fixture: ComponentFixture<CollectorItemDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule, NgbModule, HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ],
      declarations: [CollectorItemDetailsComponent],
      providers: [NgbActiveModal]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectorItemDetailsComponent);
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
        type: 'Team',
        widgets: [],
        owner: 'owner',
        owners: [],
        configurationItemBusServName: 'CIBusServName',
        configurationItemBusAppName: 'CIBusAppName',
        validServiceName: true,
        validAppName: true,
        remoteCreated: false,
        scoreEnabled: false,
        scoreDisplay: 'HEADER',
        activeWidgets: [],
        createdAt: 0,
        updatedAt: 0,
        errorCode: 0
      };

    component.getJsonHtml(mockData);
  });

});
