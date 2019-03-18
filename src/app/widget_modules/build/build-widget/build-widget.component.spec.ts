import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import { BuildWidgetComponent } from './build-widget.component';

describe('BuildWidgetComponent', () => {
    let component: BuildWidgetComponent;
    let fixture: ComponentFixture<BuildWidgetComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
              HttpClientTestingModule
            ],
            declarations: [BuildWidgetComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(BuildWidgetComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
