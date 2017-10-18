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
                   $scope.displayName = metricData.displayName;
                   $scope.displaySymbol = metricData.displaySymbol;
                   $scope.scorePerCommit = metricData.scorePerCommit;
                   $scope.commitMatchRegex = metricData.commitMatchRegex;
               } else {
                   $scope.commitMessageMatch = false;
                   $scope.description = metricData.description;
                   $scope.displayName = metricData.displayName;
                   $scope.displaySymbol = metricData.displaySymbol;
                   $scope.rangeMatrix = [];
                   metricData.scoreRanges.forEach(function (rangeData) {
                       var range = (rangeData.rangeMin == rangeData.rangeMax) ? "VALUE = " + rangeData.rangeMax : rangeData.rangeMin + "  <= VALUE <=  " + rangeData.rangeMax;
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
