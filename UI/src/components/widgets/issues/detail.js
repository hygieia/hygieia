(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('IssueDetailController', IssueDetailController);

    IssueDetailController.$inject = ['$modalInstance', 'issues', 'DashStatus'];
    function IssueDetailController($modalInstance, issues, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.issues = issues;

    }
})();
