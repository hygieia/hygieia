import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { async, ComponentFixture, TestBed } from "@angular/core/testing"
import { BuildViewerComponent } from './build-viewer.component';

describe('BuildViewerComponent', () => {
    let component: BuildViewerComponent;
    let fixture: ComponentFixture<BuildViewerComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ BuildViewerComponent ],
            providers: [],
            schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
            imports: []
        })
        .compileComponents();
    }));

    afterEach(() => {
        fixture.destroy();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(BuildViewerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
})