(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('IssueClosedDetailController', IssueClosedDetailController);

    IssueClosedDetailController.$inject = ['$modalInstance', 'issues', 'DashStatus'];
    function IssueClosedDetailController($modalInstance, issues, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.issues = issues;

    }
})();
