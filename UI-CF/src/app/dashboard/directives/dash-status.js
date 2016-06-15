/**
 * Standard status icon for various widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')

        // status constant so widgets can use the same values as an enum
        .constant('DashStatus', {
            PASS: 1,
            WARN: 2,
            FAIL: 3
        })
        .directive('dashStatus', dashStatus);

    dashStatus.$inject = ['DashStatus'];
    function dashStatus(DashStatus) {
        return {
            scope: {
                status: '@dashStatus',
                failText: '@dashStatusFailText'
            },
            restrict: 'A',
            controller: controller,
            link: link,
            templateUrl: 'app/dashboard/views/dash-status.html'
        };

        function controller($scope) {
            $scope.statuses = DashStatus;
        }

        function link(scope, element, attrs, containerCtrl) {
            scope.failText = scope.failText || '!';

            attrs.$observe('dashStatus', function() {
                // accept a bunch of different statuses
                switch (scope.status.toLowerCase()) {
                    case 3:
                    case '3':
                    case 'false':
                    case 'alert':
                        scope.currentStatus = DashStatus.FAIL;
                        break;
                    case 1:
                    case '1':
                    case 'true':
                    case 'ok':
                        scope.currentStatus = DashStatus.PASS;
                        break;
                    case 2:
                    case '2':
                    case 'warning':
                        scope.currentStatus = DashStatus.WARN;
                        break;
                    default:
                        break;
                }
            });

        }

    }
})();