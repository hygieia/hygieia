/**
 * Chartist.js plugin to display a data label on top of the points in a line chart.
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        labelClass: 'ct-label',
        labelOffset: {
            x: 0,
            y: -10
        },
        textAnchor: 'middle'
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.ctPointLabels = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function ctPointLabels(chart) {
            if (chart instanceof Chartist.Line) {
                chart.on('draw', function (data) {
                    if (data.type === 'point') {
                        data.group.elem('text', {
                            x: data.x + options.labelOffset.x,
                            y: data.y + options.labelOffset.y,
                            style: 'text-anchor: ' + options.textAnchor
                        }, options.labelClass).text(data.value.y);
                    }
                });
            }
        };
    };

}(window, document, Chartist));