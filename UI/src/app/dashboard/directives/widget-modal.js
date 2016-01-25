/**
 * For use around the configuration view's content to provide a consistent design
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('widgetModal', widgetModal);

    function widgetModal() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                title: '@widgetModalTitle'
            },
            template: '<div class="widget-modal">' +
            '<div class="widget-modal-heading" ng-if="title">{{::title}}</div>' +
            '<div class="widget-modal-body" ng-transclude></div>' +
            '</div>'
        };
    }
})();