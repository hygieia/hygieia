(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('OrgsRepoDetailsController', OrgsRepoDetailsController);

    OrgsRepoDetailsController.$inject = ['$modalInstance', 'commits', 'DashStatus'];
    function OrgsRepoDetailsController($modalInstance, commits, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;

    }
})();