/**
 * Score as part of widget
 * On click on score view score details
 */


(function(){
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .controller('widgetScoreCtrl', widgetScoreCtrl)
        .directive('widgetScore',widgetScore);


    widgetScoreCtrl.$inject = ['$scope', '$rootScope', '$uibModal'];
    function widgetScoreCtrl($scope, $rootScope, $uibModal){
        var vm = $scope;
        vm.getScoreClass = getScoreClass;
        vm.viewDetails = viewDetails;

        activate();

        function activate() {
        }

        function getScoreClass() {
            if (vm.ngModel.alert) {
                return 'low';
            }
            return '';
        }

        function viewDetails() {
            $uibModal.open({
                templateUrl: 'app/dashboard/views/scoreWidgetDetails.html',
                controller: 'ScoreWidgetDetailsController',
                controllerAs: 'detail',
                size: 'lg',
                resolve: {
                    scoreWidget: function() {
                        return vm.ngModel;
                    }
                }
            });
        };
    }



    function widgetScore() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                ngModel: '=',
                refId: '@'
            },
            templateUrl: 'app/dashboard/views/widgetScore.html',
            controller: 'widgetScoreCtrl'
        };
    }


})();
