(function(angular) {
    'use strict';
    function TestListController($uibModal) {
        var ctrl = this;

        ctrl.details = function(test) {
            $uibModal.open({
                controller: 'TestDetailsController',
                controllerAs: 'testDetails',
                templateUrl: 'components/widgets/codeanalysis/testdetails.html',
                size: 'lg',
                resolve: {
                    testResult: function () {
                        return test;
                    }
                }
            });
        };

    }

    angular.module(HygieiaConfig.module).component('testList', {
        templateUrl: 'components/widgets/codeanalysis/testList.html',
        controller: TestListController,
        bindings: {
            testTitle: '@',
            testData: '<'
        }
    });
})(window.angular);
