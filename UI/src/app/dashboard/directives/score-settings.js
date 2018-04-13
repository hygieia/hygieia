/**
 * Score settings in create/edit dashboard screen
 */


(function(){
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .controller('scoreSettingsCtrl', scoreSettingsCtrl)
        .directive('scoreSettings', scoreSettings);


    scoreSettingsCtrl.$inject = ['$scope', 'ScoreDisplayType'];
    function scoreSettingsCtrl($scope, ScoreDisplayType){
        var vm = $scope;
        vm.selectHeaderOrWidgetToolTip = "Dashboard score can either be displayed in header or as a widget.";
        vm.scoreDisplayType = ScoreDisplayType;
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
