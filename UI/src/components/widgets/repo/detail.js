(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$modalInstance', 'commits', 'DASH_STATUS'];
    function RepoDetailController($modalInstance, commits, DASH_STATUS) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DASH_STATUS;
        ctrl.commits = commits;

        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }
    }
})();