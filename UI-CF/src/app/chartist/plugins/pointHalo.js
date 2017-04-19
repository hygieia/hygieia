/**
 * Chartist.js plugin to display a halo around the point's tip
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.pointHalo = function () {

        return function pointHalo(chart) {
            if (!(chart instanceof Chartist.Line)) {
                return;
            }

            chart.on('draw', function(data) {
                if (data.type === 'point') {
                    data.group.append(new Chartist.Svg('circle', {
                        cx: data.x,
                        cy: data.y,
                        r: 3
                    }, 'ct-point-halo'), true);
                }
            });
        };
    };

}(window, document, Chartist));
