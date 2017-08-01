(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('wlmonitorDetailController', wlmonitorDetailController);

    wlmonitorDetailController.$inject = ['$uibModalInstance', 'environment', 'collectorName', 'DashStatus'];
    function wlmonitorDetailController($uibModalInstance, environment, collectorName, DashStatus) {
        /*jshint validthis:true */
    	console.log(environment);
        var ctrl = this;
        ctrl.environment = environment;
        ctrl.collectorName = collectorName;
        ctrl.close = close;
        console.log(ctrl.environment);
        function close() {
            $uibModalInstance.dismiss('close');
        }
    }
})();
