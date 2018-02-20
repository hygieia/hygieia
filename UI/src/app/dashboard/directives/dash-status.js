/**
 * Standard status icon for various widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')

        // status constant so widgets can use the same values as an enum
        .constant('DashStatus', {
            IGNORE: 0,
        	PASS: 1,
            WARN: 2,
            FAIL: 3,
            UNAUTH: 4,
            CRITICAL: 5
        })
        .directive('dashStatus', dashStatus);

    dashStatus.$inject = ['DashStatus'];
    function dashStatus(DashStatus) {
        return {
            scope: {
                status: '@dashStatus',
                failText: '@dashStatusFailText',
                ignoreText: '@dashStatusIgnoreText'
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
            scope.ignoreText = scope.ignoreText || '-';

            attrs.$observe('dashStatus', function() {
                // accept a bunch of different statuses
                switch (scope.status.toLowerCase()) {
                    case 5:
                    case '5':
                    case 'critical':
                        scope.currentStatus = DashStatus.CRITICAL;
                        break;
	                case 4:
                	case '4':
                	case 'unauth':
                		scope.currentStatus = DashStatus.UNAUTH;
                		break;
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
                    case 0:
                    case '0':
                    case 'ignore':
                        scope.currentStatus = DashStatus.IGNORE;
                        break;
                    default:
                        break;
                }
            });

        }

    }
})();