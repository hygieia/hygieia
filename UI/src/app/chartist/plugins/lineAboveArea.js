/**
 * Chartist.js plugin to display lines at the middle and ends of the chart
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.lineAboveArea = function () {

        return function lineAboveArea(chart) {
            chart.on('created', function (data) {
                var areas = data.svg.querySelectorAll('.ct-area');
                if(areas) {
                    for(var x=0;x<areas.svgElements.length;x++) {
                        var area = areas.svgElements[x]._node;
                        area.parentNode.insertBefore(area, area.parentNode.firstChild);
                    }
                }
            });
        };
    };

}(window, document, Chartist));