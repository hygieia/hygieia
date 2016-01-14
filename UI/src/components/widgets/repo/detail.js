(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$modalInstance', 'commits', 'DashStatus'];
    function RepoDetailController($modalInstance, commits, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;

        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();