(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('DeployDetailController', DeployDetailController);

    DeployDetailController.$inject = ['$modalInstance', 'environment', 'collectorName', 'DASH_STATUS',];
    function DeployDetailController($modalInstance, environment, collectorName, DASH_STATUS) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DASH_STATUS;
        ctrl.environment = environment;
        ctrl.collectorName = collectorName;

        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();
