/**
 * Controller for Score View : Header, Widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('ScoreViewController', ScoreViewController);

    ScoreViewController.$inject = ['$scope', 'scoreData', '$q', '$uibModal', 'scoreDataService'];
    function ScoreViewController($scope, scoreData, $q, $uibModal, scoreDataService) {
        var ctrl = $scope;

        ctrl.load = load;
        ctrl.viewDetails = viewDetails;
        ctrl.getScoreClass = getScoreClass;

        ctrl.scoreViewInfoToolTip = "Overall score for your dashboard. Click on score to view more details";

        load();

        function load() {
            var deferred = $q.defer();
            scoreData.details($scope.dashboardId).then(function(data) {
                var result = data.result;
                processResponse(result);
                scoreDataService.addDashboardScore(result);
                var lastUpdated = data.lastUpdated;
                ctrl.lastUpdatedActual = lastUpdated;
                ctrl.lastUpdatedDisplay = moment(lastUpdated).dash('ago');
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        }


        function processResponse(data) {
            ctrl.data = data;
            if (data) {
                ctrl.rateItOptions.value = data.score;
            } else {
                ctrl.rateItOptions.value = 'N/A';
            }
        }

        function viewDetails() {
            $uibModal.open({
                templateUrl: 'app/dashboard/views/scoreComponentDetails.html',
                controller: 'ScoreComponentDetailsController',
                controllerAs: 'detail',
                size: 'lg',
                resolve: {
                    scoreComponent: function() {
                        return ctrl.data;
                    }
                }
            });
        }

        function getScoreClass() {
            if (ctrl.data && ctrl.data.alert) {
                return 'low';
            }
            return '';
        }
    }
})();
