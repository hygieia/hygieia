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
        ctrl.scoreTxt = '--';

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

            var adjustVal = 100/data.total;
            ctrl.pieData = {
                                   labels: ['Score', ''],
                                   series: [parseInt(data.score * adjustVal), parseInt((data.total - data.score) * adjustVal)]
                           };

            ctrl.scoreTxt =  "Score: " + data.score + " / " + data.total;
        }



        function defaultStateCallback(isDefaultState) {
            //$scope.$apply(function() {
                $scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
            //});
        }

        function environmentsCallback(data) {
            //$scope.$apply(function () {
                ctrl.environments = data.environments;
            //});
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
