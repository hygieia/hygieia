/**
 * Detail controller for the build widget
 */
(function () {

    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SubnetDetailController', SubnetDetailController);

    SubnetDetailController.$inject = ['$scope', '$uibModalInstance', 'subnet', '$uibModal'];
    function SubnetDetailController($scope, $uibModalInstance, subnet, $uibModal) {

        var ctrl = this;
        ctrl.subnet = subnet;
        ctrl.close = close;

        function close() {
            $uibModalInstance.dismiss('close');
        }

    }
})();
