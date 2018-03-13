/**
 * Score settings in create/edit dashboard screen
 */


(function(){
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .controller('scoreSettingsCtrl', scoreSettingsCtrl)
        .directive('scoreSettings', scoreSettings);


    scoreSettingsCtrl.$inject = ['$scope'];
    function scoreSettingsCtrl($scope){
        var vm = $scope;
    }



    function scoreSettings() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                ngModel: '='
            },
            templateUrl: 'app/dashboard/views/scoreSettings.html',
            controller: 'scoreSettingsCtrl'
        };
    }


})();
