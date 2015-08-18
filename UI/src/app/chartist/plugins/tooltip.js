/**
 * Chartist.js plugin to display a tooltip when hovered over charts
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {

    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.tooltip = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function tooltip(chart) {
            if (!(chart instanceof Chartist.Line) || !(chart instanceof Chartist.Bar)) {
                return;
            }

            chart.on('draw', function(data) {
                if (data.type === 'point') {

                    var area = new Chartist.Svg('line', {
                        x1: data.x,
                        x2: data.x +0.01,
                        y1: data.y,
                        y2: data.y
                    }, 'ct-tooltip-trigger-area');

                    area._node.setAttribute('ct:value', data.value);
                    /*var parent = data.group._node.parentNode;
                    parent.insertBefore(area._node, parent.firstChild);*/

                    data.group.append(area);
                }
            });

            chart.on('created', function (data) {
                var triggerClass = chart instanceof Chartist.Line ?
                    '.ct-tooltip-trigger-area' :
                    '.ct-bar';
                var areas = data.svg.querySelectorAll(triggerClass);
                if(!areas) {
                    return;
                }

                var svgParent = data.svg._node.parentNode;
                var tooltip = svgParent.children[0];
                var tooltipContent;
                if(tooltip.getAttribute('class') != 'tooltip top') {
                    tooltip = document.createElement('div');
                    tooltip.setAttribute('class', 'tooltip top');

                    var arrow = document.createElement('div');
                    arrow.setAttribute('class', 'tooltip-arrow');
                    tooltip.appendChild(arrow);

                    tooltipContent = document.createElement('div');
                    tooltipContent.setAttribute('class', 'tooltip-inner');
                    tooltip.appendChild(tooltipContent);

                    tooltipContent = angular.element(tooltipContent);

                    svgParent.insertBefore(tooltip, svgParent.firstChild);
                }

                for(var x=0; x<areas.svgElements.length; x++) {
                    var node=areas.svgElements[x];

                    angular.element(node._node).bind('mouseenter', function(event) {
                        tooltipContent.html(Math.round(angular.element(this).attr('ct:value') * 100) / 100);

                        angular.element(tooltip)
                            .css({
                                display: 'block',
                                visibility: 'hidden'
                            });

                        var left = ((event.offsetX || event.originalEvent.layerX) - tooltip.offsetWidth / 2) + 'px';
                        var top = ((event.offsetY || event.originalEvent.layerY) - tooltip.offsetHeight - 10) + 'px';
                        angular.element(tooltip).css({
                            position: 'absolute',
                            left: left,
                            top: top,
                            opacity: 1,
                            visibility: 'visible'
                        });
                    });

                    angular.element(node._node).bind('mouseleave', function() {
                        angular.element(tooltip).css({
                            display: 'none'
                        });
                    });
                }
            });
        };
    };

}(window, document, Chartist));
