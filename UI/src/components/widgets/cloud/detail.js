/**
 * Detail controller for the cloud widget
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('CloudWidgetDetailController', CloudWidgetDetailController);

    CloudWidgetDetailController.$inject = ['$scope', '$modalInstance', 'getType'];
    function CloudWidgetDetailController($scope, $modalInstance, getType) {
        var ctrl = this;
        ctrl.close = close;
        ctrl.type = getType;

        // ctrl.collectorName = collectorName;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
