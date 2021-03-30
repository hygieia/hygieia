import { HttpClientTestingModule } from "@angular/common/http/testing";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { async, ComponentFixture, TestBed } from "@angular/core/testing"
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { BuildViewerComponent } from './build-viewer.component';

describe('BuildViewerComponent', () => {
    let component: BuildViewerComponent;
    let fixture: ComponentFixture<BuildViewerComponent>;
    let router: Router;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ BuildViewerComponent ],
            providers: [{ provide: Router, useValue: {
                navigate: jasmine.createSpy('navigate')
            }}],
            imports: [ FormsModule ]
        })
        .compileComponents();
    }));

    afterEach(() => {
        fixture.destroy();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(BuildViewerComponent);
        component = fixture.componentInstance;
        router = TestBed.get(Router);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should navigate to standalone build page', () => {
        component.buildId = "1234";
        component.searchBuild();
        expect(router.navigate).toHaveBeenCalledWith(['/build/1234']);
    })  
})