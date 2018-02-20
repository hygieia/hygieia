(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('scoreViewController', scoreViewController);

    scoreViewController.$inject = ['$scope', 'DashStatus', 'scoreData', 'DisplayState', '$q', '$uibModal', 'scoreDataService'];
    function scoreViewController($scope, DashStatus, scoreData, DisplayState, $q, $uibModal, scoreDataService) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.pieOptions = {
                    donut: true,
                    donutWidth: 20,
                    startAngle: 270,
                    showLabel: false
        };

        ctrl.rateItOptions = {
            readOnly : true,
            step : 0.1,
            starWidth : 44,
            starHeight : 44
        };

        ctrl.data = {};
        ctrl.load = load;

        function load() {
            var deferred = $q.defer();
            scoreData.details($scope.widgetConfig.componentId).then(function(data) {
                var result = data.result;
                processResponse(result);
                scoreDataService.addDashboardScore(result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        }


        function processResponse(data) {
            ctrl.data = data;
            ctrl.rateItOptions.value = data.score;
        }

        ctrl.viewDetails = function() {
            $uibModal.open({
                templateUrl: 'app/dashboard/views/scoreWidgetDetails.html',
                controller: 'ScoreWidgetDetailsController',
                controllerAs: 'detail',
                size: 'lg',
                resolve: {
                    scoreWidget: function() {
                        return ctrl.data;
                    }
                }
            });
        };



    }
})();
