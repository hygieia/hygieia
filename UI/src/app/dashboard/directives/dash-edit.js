/**
 * Standard trash icon for various widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('dashEdit', function () {
            return {
                transclude: true,
                template: '<span class="clickable fa fa-stack">' +
                '<span class="fa-circle-thin fa-stack-2x text-success"></span>' +
                '<span class="fa-pencil-square-o fa-stack-1x text-success"></span>' +
                '</span>'
            };
        });
})();