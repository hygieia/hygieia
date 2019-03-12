import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildWidgetComponent } from './build-widget.component';
import { HttpClientModule } from '@angular/common/http';

describe('BuildWidgetComponent', () => {
    let component: BuildWidgetComponent;
    let fixture: ComponentFixture<BuildWidgetComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule
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
