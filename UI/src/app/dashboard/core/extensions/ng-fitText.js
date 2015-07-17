/*
 * Angular FitText
 * Pulled from: https://github.com/patrickmarabeas/ng-FitText.js
 *
 * Modified to support resizing of child elements based on selector
 */

(function(window, document, angular, undefined) {

    'use strict';

    angular.module('fitText', [])
        .value( 'config', {
            'debounce': false,
            'delay': 250,
            'min': undefined,
            'max': undefined
        })

        .directive('fitText', ['$timeout', 'config', 'fitTextConfig', function($timeout, config, fitTextConfig) {
            return {
                restrict: 'A',
                scope: true,
                link: function(scope, element, attrs) {
                    angular.extend(config, fitTextConfig.config);

                    var elements = (attrs.fitText ? angular.element(element[0].querySelectorAll(attrs.fitText)) : element)
                        .css({
                            display: 'inline-block',
                            whiteSpace: 'nowrap',
                            lineHeight: '1'
                        });

                    var compressor = attrs.fitTextCompressor || 1;
                    var minFontSize = attrs.fitTextMin || config.min || Number.NEGATIVE_INFINITY;
                    var maxFontSize = attrs.fitTextMax || config.max || Number.POSITIVE_INFINITY;

                    var resizer = function() {
                        $timeout( function() {
                            var size = null;
                            for(var x=0;x<elements.length;x++) {
                                var el = elements[x];

                                var parent = el.parentNode;
                                var ratio = el.offsetHeight / el.offsetWidth;
                                var calculatedSize = Math.max(
                                    Math.min(parent.offsetWidth * ratio * compressor,
                                        parseFloat(maxFontSize)
                                    ),
                                    parseFloat(minFontSize)
                                );

                                if(!isNaN(ratio) && (size === null || calculatedSize < size)) {
                                    size = calculatedSize;
                                }
                            }

                            elements.css('font-size', size + 'px');

                        },50);
                    };
                    resizer();

                    for(var x=0;x<elements.length;x++) {
                        var el = elements[x];
                        scope.$watch(function() {
                            return el.innerText;
                        }, resizer);
                    }

                    config.debounce ?
                        angular.element(window).bind('resize', config.debounce(function(){ scope.$apply(resizer);}, config.delay)) :
                        angular.element(window).bind('resize', function(){ scope.$apply(resizer);});
                }
            };
        }])

        .provider('fitTextConfig', function() {
            var self = this;
            this.config = {};
            this.$get = function() {
                var extend = {};
                extend.config = self.config;
                return extend;
            };
            return this;
        });

})(window, document, angular);