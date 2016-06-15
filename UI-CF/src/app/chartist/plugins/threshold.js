/**
 * Chartist.js plugin to display a single line at a certain point on a chart.
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        threshold: null
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.threshold = function (options) {

        if (!options || !options.threshold) {
            throw new Error('"threshold" not set');
        }

        options = Chartist.extend({}, defaultOptions, options);

        return function threshold(chart) {
            var points = [];

            chart.on('draw', function (data) {
                var field = null;
                if (chart instanceof Chartist.Line && data.type === 'point') {
                    field = 'y';
                }
                else if (chart instanceof Chartist.Bar && data.type === 'bar') {
                    field = 'y2';
                }
                if (field !== null && !points.length || points.length == 1 && points[0][0] != data.value) {
                    points.push([data.value, data[field]]);
                }
            });

            chart.on('created', function (data) {
                if (points.length == 2 && options.threshold) {
                    // calculate slope

                    var minY = data.bounds.min,
                        maxY = data.bounds.max;

                    // don't draw it if we don't need to
                    if(minY > options.threshold || maxY < options.threshold) {
                        return;
                    }

                    var height = data.chartRect.height();
                    var y = (height - height * options.threshold / (maxY - minY)) + data.chartRect.padding.top;

                    // draw line
                    data.svg.elem('line', {
                        x1: data.chartRect.x1,
                        y1: y,
                        x2: data.chartRect.x2,
                        y2: y
                    }, ['ct-grid ct-grid-threshold'].join(' '));
                }
            });
        };
    };

}(window, document, Chartist));