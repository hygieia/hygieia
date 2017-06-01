(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .directive('guageContainer', guageContainer);

    function guageContainer() {
        return {
            restrict: 'EA',
            link: link
        };
    }

    function link(scope, element, attrs) {
        var $chart = element.children('.ct-wrapper');

        element.css('overflow', 'hidden');

        resize();

        angular.element(window).on('resize', resize);

        function resize() {
            element[0].style.height = ($chart[0].offsetHeight / 2) + 'px';
        }
    }
})();
