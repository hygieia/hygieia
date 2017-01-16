/**
 * Chartist.js plugin to display lines at the middle and ends of the chart
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.gridBoundaries = function () {

        return function gridBoundaries(chart) {
            chart.on('draw', function(data) {
                // remove any elements if showGrid is still on
                if(data.type == 'grid') {
                    if(typeof data.element._node.remove=='function') {
                        data.element._node.remove();
                    } else {
                        while(data.element._node.hasChildNodes()) {
                            data.element._node.removeChild(_node.firstChild);
                        }
                    }
                }
            });

            chart.on('created', function (data) {
                var rect = data.chartRect;

                var lines = [
                    // bottom horizontal
                    {
                        x1: rect.x1,
                        x2: rect.x2,
                        y1: rect.y1,
                        y2: rect.y1,
                        placement: 'bottom-x'
                    },
                    // top horizontal
                    {
                        x1: rect.x1,
                        x2: rect.x2,
                        y1: rect.y2,
                        y2: rect.y2,
                        placement: 'top-x'
                    },
                    // middle horizontal
                    {
                        x1: rect.x1,
                        x2: rect.x2,
                        y1: (rect.y1 - rect.y2) / 2,
                        y2: (rect.y1 - rect.y2) / 2,
                        placement: 'middle-x'
                    },
                    // left vertical
                    {
                        x1: rect.x1,
                        x2: rect.x1,
                        y1: rect.y1,
                        y2: rect.y2,
                        placement: 'left-y'
                    },
                    // right vertical
                    {
                        x1: rect.x2,
                        x2: rect.x2,
                        y1: rect.y1,
                        y2: rect.y2,
                        placement: 'right-y'
                    },
                    // middle vertical
                    {
                        x1: (rect.x2 + rect.x1) / 2,
                        x2: (rect.x2 + rect.x1) / 2,
                        y1: rect.y1,
                        y2: rect.y2,
                        placement: 'middle-y'
                    }
                ];

                for (var x = 0; x < lines.length; x++) {
                    var line = lines[x];

                    data.svg.querySelector('.ct-grids').elem('line', {
                        x1: line.x1,
                        x2: line.x2,
                        y1: line.y1,
                        y2: line.y2
                    }, ['ct-grid', 'ct-grid-' + line.placement].join(' '));


                }
            });
        };
    };

}(window, document, Chartist));