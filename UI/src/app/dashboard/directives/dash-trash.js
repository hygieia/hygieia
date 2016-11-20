/**
 * Standard trash icon for various widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('dashTrash', function () {
            return {
                transclude: true,
                template: '<span class="clickable fa fa-stack">' +
                    '<span class="fa-circle-thin fa-stack-2x text-danger"></span>' +
                    '<span class="fa-trash fa-stack-1x text-danger"></span>' +
                    '</span>'
            };
        });
})();