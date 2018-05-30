(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RallyGraphDetailController', RallyGraphDetailController);

    RallyGraphDetailController.$inject = ['$scope','$uibModalInstance', '$uibModal', 'iterationBurnData', 'planEstimate', 'label'];
    function RallyGraphDetailController($scope,$uibModalInstance, $uibModal, iterationBurnData, planEstimate, label) {

        var maxYaxisAccepted = (20-(parseInt(planEstimate)%20))+parseInt(planEstimate);
        var ctrl = this;
        var data = iterationBurnData;
/*****Start Iteration burndown charts configuration *****/
  $scope.labels = label;
  $scope.colors = ['#5c9acb', '#696969', '#7fb17f'];
  $scope.data = iterationBurnData;
  $scope.datasetOverride = [
    { yAxisID: 'y-axis-1',label: "Task To Do (Hours)",type: 'bar',backgroundColor: "rgba(92,154,203,1)"}, 
    { yAxisID: 'y-axis-1',label: "Ideal (Hours)",type: 'line',borderColor: "rgba(105,105,105,1)", fill: false  }, 
    { yAxisID: 'y-axis-2',label: "Accepted (Points)",type: 'bar',backgroundColor: "rgba(127,177,127,1)"  }];
  $scope.options = {
    scales: {
      yAxes: [
        {
           id: 'y-axis-1',
          type: 'linear',
          display: true,
          position: 'right'
        },
        {
          id: 'y-axis-1',
          type: 'linear',
          display: true,
          position: 'left',
          scaleLabel:{
            display: true,
            labelString:"Task To Do (Hours)"
          },
            ticks: {
                beginAtZero:true,
                min: 0
            }
        },
        {
          id: 'y-axis-2',
          type: 'linear',
          display: true,
          position: 'right',
          scaleLabel:{
            display: true,
            labelString:"Accepted (Points)"
          },
            ticks: {
                beginAtZero:true,
                min: 0,
                max: maxYaxisAccepted,
                stepSize: 20
            }
        }
      ], xAxes: [{
      scaleLabel: {
        display: true,
        labelString: 'Date'
      }
    }]
    }
  };
/*****End Iteration burndown charts configuration *****/
    }
})();