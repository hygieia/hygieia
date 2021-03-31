import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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
        component.buildId = '1234';
        component.searchBuild();
        expect(router.navigate).toHaveBeenCalledWith(['/build/1234']);
    });
});
