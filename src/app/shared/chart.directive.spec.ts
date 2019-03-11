import { ChartDirective } from './chart.directive';
import { ViewContainerRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';

describe('ChartDirective', () => {
    it('should create an instance', () => {
        const viewContainerRef = TestBed.get(ViewContainerRef) as ViewContainerRef;
        const directive = new ChartDirective(viewContainerRef);
        expect(directive).toBeTruthy();
    });
});
