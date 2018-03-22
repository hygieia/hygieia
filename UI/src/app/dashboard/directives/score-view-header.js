/**
 * Score View for Header
 */


(function(){
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('scoreViewHeader', scoreViewHeader);


    function scoreViewHeader() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                rateItOptions: '=',
                dashboardId: '@'
            },
            templateUrl: 'app/dashboard/views/scoreViewHeader.html',
            controller: 'ScoreViewController'
        };
    }


})();
