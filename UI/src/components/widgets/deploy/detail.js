(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('DeployDetailController', DeployDetailController);

    DeployDetailController.$inject = ['$modalInstance', 'environment', 'collectorName', 'collectorNiceName', 'DashStatus'];
    function DeployDetailController($modalInstance, environment, collectorName, collectorNiceName, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.environment = environment;
        ctrl.collectorName = collectorName;
        ctrl.collectorNiceName = collectorNiceName;
        
        ctrl.deployUrlNiceName = deployUrlNiceName;

        ctrl.close = close;

        function deployUrlNiceName() {
            if (!isEmpty(collectorNiceName)) {
                return collectorNiceName;
            } else {
                return collectorName;
            }
        }

        function isEmpty(str) {
            //!str returns true for uninitialized, null and empty strings
            //the test checks if the string only contains whitespaces and returns true.
            return !str || /^[\s]*$/.test(str);
        }
        
        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
