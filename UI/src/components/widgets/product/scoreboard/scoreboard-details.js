(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('scoreBoardDetailsController', scoreBoardDetailsController);

    scoreBoardDetailsController.$inject = ['$scope', '$uibModalInstance', 'scoreBoardDetailsConfig'];
    function scoreBoardDetailsController($scope, $uibModalInstance, scoreBoardDetailsConfig) {
        /*jshint validthis:true */
        var ctrl = this;
        ctrl.isScoreDefined = isScoreDefined;

        $scope.metricName = scoreBoardDetailsConfig.metricName;
        $scope.teamName = scoreBoardDetailsConfig.teamName;
        $scope.score = scoreBoardDetailsConfig.metricScore;
        $scope.value = scoreBoardDetailsConfig.metricValue;
        $scope.commitMessageMatch = false;

        scoreBoardDetailsConfig.scoreBoardMetrics.forEach(function(metricData) {

           if(metricData.metricName == $scope.metricName) {
               if(metricData.metricName.match("^commitMessageMatch")){
                   $scope.commitMessageMatch = true;
                   $scope.description = metricData.description;
                   $scope.formattedName = metricData.formattedName;
                   $scope.symbol = metricData.symbol;
                   $scope.scorePerCommit = metricData.scorePerCommit;
                   $scope.commitMatchRegex = metricData.commitMatchRegex;
               } else {
                   $scope.commitMessageMatch = false;
                   $scope.description = metricData.description;
                   $scope.formattedName = metricData.formattedName;
                   $scope.symbol = metricData.symbol;
                   $scope.rangeMatrix = [];
                   metricData.gamificationRangeScores.forEach(function (rangeData) {
                       var range = (rangeData.min == rangeData.max) ? "VALUE = " + rangeData.max : rangeData.min + "  <= VALUE <=  " + rangeData.max;
                       var rangeMatrixElement = {
                           range: range,
                           score: rangeData.score
                       };
                       $scope.rangeMatrix.push(rangeMatrixElement);
                   });
               }
           }
        });

        function isScoreDefined() {
            return $scope.score != -1;
        }
    }
})();
