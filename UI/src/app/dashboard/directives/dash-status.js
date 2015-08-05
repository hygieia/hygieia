/**
 * Standard status icon for various widgets
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')

        // status constant so widgets can use the same values as an enum
        .constant('DASH_STATUS', {
            PASS: 1,
            WARN: 2,
            FAIL: 3
        })
        .directive('dashStatus', dashStatus);

    dashStatus.$inject = ['DASH_STATUS'];
    function dashStatus(DASH_STATUS) {
        return {
            scope: {
                status: '@dashStatus',
                failText: '@dashStatusFailText'
            },
            restrict: 'A',
            controller: controller,
            link: link,
            templateUrl: 'app/dashboard/views/dash-status.html'
            //template: getTemplate
        };

        function controller($scope) {
            $scope.statuses = DASH_STATUS;
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
                        scope.currentStatus = DASH_STATUS.FAIL;
                        break;
                    case 1:
                    case '1':
                    case 'true':
                    case 'ok':
                        scope.currentStatus = DASH_STATUS.PASS;
                        break;
                    case 2:
                    case '2':
                    case 'warning':
                        scope.currentStatus = DASH_STATUS.WARN;
                        break;
                    default:
                        break;
                }
            });

        }

        //function getTemplate() {
        //    return
        //    '<span class="dash-status fa">' +
        //    '<span class="dash-status-pass fa-check" ng-if="currentStatus == statuses.PASS"></span>' +
        //    '<span class="dash-status-warn fa-warning" ng-if="currentStatus == statuses.WARN"></span>' +
        //    '<span class="dash-status-fail fa-exclamation-circle fa-lg" ng-if="currentStatus == statuses.FAIL"></span>' +
        //    '</span>';
        //}
    }
})();