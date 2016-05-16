(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PullDetailController', PullDetailController);

    PullDetailController.$inject = ['$modalInstance', 'organizations', 'DashStatus'];
    function PullDetailController($modalInstance, organizations, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.organizations = organizations;

    }
})();
