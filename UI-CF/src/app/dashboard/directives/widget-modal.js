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
            scope: true,
            link: function ($scope, $element, $attributes) {

                $scope.title = $attributes.widgetModalTitle;
                $scope.close = $attributes.widgetModalClose != 'false';
            },
            template: '<div class="widget-modal">' +
            '<button type="button" class="widget-modal-close" ng-click="$close()" ng-if="close" aria-hidden="true">&times;</button>' +
            '<div class="widget-modal-heading" ng-if="title">{{title}}</div>' +
            '<div class="widget-modal-body" ng-transclude></div>' +
            '</div>'
        };
    }
})();