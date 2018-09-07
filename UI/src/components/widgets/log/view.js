(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LogViewController', RepoViewController);

    LogViewController.$inject = ['$q', '$scope','logRepoData', 'collectorData', '$uibModal'];

    function LogViewController($q, $scope, logRepoData, collectorData, $uibModal) {

    }
})();