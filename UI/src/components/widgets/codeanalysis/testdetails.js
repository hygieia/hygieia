(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('TestDetailsController', TestDetailsController);

    TestDetailsController.$inject = ['$scope','$modalInstance', 'testResult', 'DashStatus'];
    function TestDetailsController($scope, $modalInstance, testResult, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.testResult = testResult;
        ctrl.duration = msToTime(testResult.duration);
        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }

        $scope.showCapabilityDetail = function (capability) {
            if ($scope.activeCapability != capability.description) {
                $scope.activeCapability = capability.description;
            }
            else {
                $scope.activeCapability = null;
            }
        };
        $scope.showTestSuiteDetail = function (testSuite) {
            if ($scope.activeSuite != testSuite.description) {
                $scope.activeSuite = testSuite.description;
            }
            else {
                $scope.activeSuite = null;
            }
        };
        $scope.showTestCaseDetail = function (testCase) {
            if ($scope.activeCase != testCase.description) {
                $scope.activeCase = testCase.description;
            }
            else {
                $scope.activeCase = null;
            }
        };

        $scope.showStatusIcon =
        function showStatusIcon(item) {
            if (item.status.toLowerCase() == 'success') {
                return 'ok';
            } else if (item.status.toLowerCase() == 'skipped') {
                return 'warning';
            } else {
                return 'error';
            }
        };

        function msToTime(duration) {
            var milliseconds = parseInt((duration%1000)/100),
                seconds = parseInt((duration/1000)%60),
                minutes = parseInt((duration/(1000*60))%60),
                hours = parseInt((duration/(1000*60*60))%24);

            hours = (hours < 10) ? "0" + hours : hours;
            minutes = (minutes < 10) ? "0" + minutes : minutes;
            seconds = (seconds < 10) ? "0" + seconds : seconds;

            return hours + ":" + minutes + ":" + seconds;
        }
    }

})();