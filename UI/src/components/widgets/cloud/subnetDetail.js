/**
 * Detail controller for the build widget
 */
(function () {

    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SubnetDetailController', SubnetDetailController);

    SubnetDetailController.$inject = ['$scope', '$modalInstance', 'subnet', '$modal'];
    function SubnetDetailController($scope, $modalInstance, subnet, $modal) {
      
        var ctrl = this;
        ctrl.subnet = subnet;
        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }      

    }
})();
