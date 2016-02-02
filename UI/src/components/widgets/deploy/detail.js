(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('DeployDetailController', DeployDetailController);

    DeployDetailController.$inject = ['$modalInstance', 'environment', 'collectorName', 'DashStatus'];
    function DeployDetailController($modalInstance, environment, collectorName, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.environment = environment;
        ctrl.collectorName = collectorName;

        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
