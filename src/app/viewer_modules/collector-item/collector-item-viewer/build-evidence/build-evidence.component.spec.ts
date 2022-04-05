import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildEvidenceComponent } from './build-evidence.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {SharedModule} from '../../../../shared/shared.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CollectorItemModule} from '../../collector-item.module';
import {CollectorItemService} from '../../collector-item.service';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

describe('BuildEvidenceComponent', () => {
  let component: BuildEvidenceComponent;
  let fixture: ComponentFixture<BuildEvidenceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule,
        RouterTestingModule.withRoutes([]), CollectorItemModule
      ],
      declarations: [],
      providers: [NgbModal, CollectorItemService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get(): string {
                  return '123';
                },
              },
            },
          },
        }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildEvidenceComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });
});
