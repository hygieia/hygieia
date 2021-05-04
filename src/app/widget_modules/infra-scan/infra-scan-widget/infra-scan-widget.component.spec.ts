import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfraScanWidgetComponent } from './infra-scan-widget.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {InfraScanService} from '../infra-scan.service';

describe('InfraScanWidgetComponent', () => {
  let component: InfraScanWidgetComponent;
  let fixture: ComponentFixture<InfraScanWidgetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule],
      declarations: [ InfraScanWidgetComponent ],
      providers: [InfraScanService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfraScanWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
