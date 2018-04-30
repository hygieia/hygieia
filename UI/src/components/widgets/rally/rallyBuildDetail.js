(function() {
    'use strict';
    angular.module(HygieiaConfig.module)
        .controller('RallyBuildDetailController', RallyBuildDetailController);
    RallyBuildDetailController.$inject = ['$scope', '$uibModalInstance', '$uibModal', 'resultData'];

    function RallyBuildDetailController($scope, $uibModalInstance, $uibModal, resultData) {
        var ctrl = this;
        $scope.oneAtATime = true;
        $scope.allClass = true;
        $scope.groups = resultData;
    }
})();