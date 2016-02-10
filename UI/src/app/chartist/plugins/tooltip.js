/**
 * Chartist.js plugin to display a tooltip when hovered over charts
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        className: ''
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.tooltip = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function tooltip(chart) {
            if (!(chart instanceof Chartist.Line) && !(chart instanceof Chartist.Bar)) {
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

                    area._node.setAttribute('ct:value', data.value.y);
                    if(data.meta) {
                        area._node.setAttribute('ct:meta', data.meta);
                    }
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

                for(var x=0; x<areas.svgElements.length; x++) {
                    var node=areas.svgElements[x];

                    angular.element(node._node).bind('mouseenter', function(event) {
                        var tooltip = svgParent.querySelector('.tooltip');

                        if(!tooltip) {
                            tooltip = document.createElement('div');
                            tooltip.setAttribute('class', 'tooltip top');

                            var arrow = document.createElement('div');
                            arrow.setAttribute('class', 'tooltip-arrow');
                            tooltip.appendChild(arrow);

                            var content = document.createElement('div');
                            content.setAttribute('class', 'tooltip-inner');
                            tooltip.appendChild(content);

                            svgParent.insertBefore(tooltip, svgParent.firstChild);
                        }

                        var tooltipContent = angular.element(tooltip.querySelector('.tooltip-inner')),
                            el = angular.element(this),
                            text = Math.round(el.attr('ct:value') * 100) / 100,
                            meta = el.attr('ct:meta');

                        tooltipContent.attr('class', 'tooltip-inner ' + options.className);

                        if(meta) {
                            var div = document.createElement('div');
                            div.innerHTML = meta;
                            text = div.childNodes[0].nodeValue;
                        }

                        tooltipContent.html(text);

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
                        angular.element(svgParent.querySelector('.tooltip')).css({
                            display: 'none'
                        });
                    });
                }
            });
        };
    };

}(window, document, Chartist));
