/**
 * Chartist.js plugin to display a data label on top of the points in a line chart.
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        onClick: false
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.ctPointClick = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function ctPointClick(chart) {
            if (chart instanceof Chartist.Line) {
                chart.on('draw', function(data) {
                    if (data.type === 'point' && options.onClick) {
                        var node = data.element._node;
                        node.style.cursor = 'pointer';

                        node.setAttribute('ct:series-index', data.seriesIndex);
                        node.setAttribute('ct:point-index', data.index);
                        node.addEventListener('click', options.onClick);
                    }
                });
            }
        };
    };

}(window, document, Chartist));