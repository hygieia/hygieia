/**
 * Score View for Widget
 */


(function(){
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('scoreViewWidget', scoreViewWidget);


    function scoreViewWidget() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                rateItOptions: '=',
                dashboardId: '@'
            },
            templateUrl: 'app/dashboard/views/scoreViewWidget.html',
            controller: 'ScoreViewController'
        };
    }


})();
