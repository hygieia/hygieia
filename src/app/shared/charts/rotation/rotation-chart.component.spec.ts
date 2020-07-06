import {NgModule} from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TimeAgoPipe } from 'time-ago-pipe';
import { DashStatusComponent } from '../../dash-status/dash-status.component';
import {CommonModule } from '@angular/common';
import {RotationChartComponent} from './rotation-chart.component';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [],
  entryComponents: [],
})
class TestModule { }

describe('RotationChartComponent', () => {
  let component: RotationChartComponent;
  let fixture: ComponentFixture<RotationChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RotationChartComponent, TimeAgoPipe, DashStatusComponent],
      imports: [TestModule, NgbModule ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
