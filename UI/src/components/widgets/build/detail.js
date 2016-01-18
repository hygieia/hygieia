/**
 * Detail controller for the build widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('BuildWidgetDetailController', BuildWidgetDetailController);

    BuildWidgetDetailController.$inject = ['$scope', '$modalInstance', 'build', 'collectorName'];
    function BuildWidgetDetailController($scope, $modalInstance, build, collectorName) {
        var ctrl = this;

        ctrl.build = build;
        ctrl.collectorName = collectorName;

        ctrl.buildPassed = buildPassed;
        ctrl.close = close;

        function buildPassed() {
            return ctrl.build.buildStatus === 'Success';
        }

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
