import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClickListComponent } from './click-list.component';
import { TimeAgoPipe } from 'time-ago-pipe';

describe('ClickListComponent', () => {
  let component: ClickListComponent;
  let fixture: ComponentFixture<ClickListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
        declarations: [ClickListComponent, TimeAgoPipe ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClickListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
