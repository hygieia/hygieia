/**
 * For use around the configuration view's content to provide a consistent design
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .directive('widgetModal', widgetModal);

    function widgetModal() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                title: '@widgetModalTitle'
            },
            // TODO: use the modal classes instead of panels
            template: '<div class="widget-modal">' +
            '<div class="widget-modal-heading">{{::title}}</div>' +
            '<div class="widget-modal-body" ng-transclude></div>' +
            '</div>'
        };
    }
})();