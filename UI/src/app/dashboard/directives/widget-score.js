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


    widgetScoreCtrl.$inject = ['$scope', '$uibModal'];
    function widgetScoreCtrl($scope, $uibModal){
        var vm = $scope;
        vm.getScoreClass = getScoreClass;
        vm.viewDetails = viewDetails;

        function getScoreClass() {
            if (vm.ngModel.alert) {
                return 'low';
            }
            return '';
        }

        function viewDetails() {
            $uibModal.open({
                templateUrl: 'app/dashboard/views/scoreComponentDetails.html',
                controller: 'ScoreComponentDetailsController',
                controllerAs: 'detail',
                size: 'lg',
                resolve: {
                    scoreComponent: function() {
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
