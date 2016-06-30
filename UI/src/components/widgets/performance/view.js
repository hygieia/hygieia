(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('performanceViewController', performanceViewController);

    performanceViewController.$inject = ['$q', '$scope','performanceData', '$modal'];
    function performanceViewController($q, $scope, performanceData, $modal) {
        var ctrl = this;

        ctrl.genericChartOptions = {
          plugins: [
            Chartist.plugins.gridBoundaries(),
            Chartist.plugins.lineAboveArea(),
            Chartist.plugins.pointHalo(),
            Chartist.plugins.ctPointClick({
              //TODO
            }),
            Chartist.plugins.axisLabels({
              axisX: {
                labels: [

                ]
              }
            }),
            Chartist.plugins.ctPointLabels({
              textAnchor: 'middle'
            })
          ],
          showArea: true,
          lineSmooth: false,
          fullWidth: true,
          axisY: {
            offset: 30,
            showGrid: true,
            showLabel: true,
            labelInterpolationFnc: function(value) {return Math.round(value * 100)/100;}
          }
        };

      ctrl.errorsChartData = {
        labels: ['A', 'b', 'c', 'd', 'e', 'f'],
        series: [
          [2, 3, 1, 5, 2, 6]
        ]
      };

        ctrl.calls = 100;

        ctrl.pieOptions = {
          donut: true,
          donutWidth: 20,
          startAngle: 270,
          total: 200,
          showLabel: false
        };
        //ctrl.showDetail = showDetail;
        ctrl.load = function() {

            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                app_Id: "blank" //to change dynamically
            };
            console.log("checkpoint1");
            performanceData.report(params).then(function(data) {
                console.log("checkpoint2");
                console.log("data: " + data);
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });

            console.log("checkpoint3");
            return deferred.promise;
        };

        /*function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'RepoDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/repo/detail.html',
                size: 'lg',
                resolve: {
                    commits: function() {
                        return groupedCommitData[pointIndex];
                    }
                }
            });
        }*/

        //var groupedCallsData = [];
        function processResponse(data) {
            //debugger;
            //ctrl.responsetime = data.responsetime;
            /*ctrl.calls = data.calls;
            ctrl.callspm = data.callspm;
            ctrl.errors = data.errors;
            ctrl.errorspc = data.errorspc;
            ctrl.errorspm = data.errorspm;
            ctrl.businesshealth = data.businesshealth;
            ctrl.nodehealth = data.nodehealth;*/
            var groupedCallsData = [];
            var labels = [];
            var count = 0;
            var nodehealth = 0;
            var businesshealth = 0;
            _(data).forEach(function(element){
                groupedCallsData.push(element.calls);
                labels.push('');
                count++;
                nodehealth += parseInt(element.nodehealth);
                businesshealth += parseInt(element.businesshealth);
            });
            console.log(groupedCallsData);
            console.log(labels);
            var nodehealthavg = Math.round(nodehealth/count * 10)/10;
            console.log("nodehealth: " + nodehealthavg);
            var businesshealthavg = Math.round(businesshealth/count * 10)/10;
            ctrl.businessavg = businesshealthavg;
            ctrl.nodeavg = nodehealthavg;
            ctrl.transactionHealthData = {
              series: [businesshealthavg, 100-businesshealthavg]
            };

            ctrl.nodeHealthData = {
              series: [nodehealthavg, 100-nodehealthavg]
            };
            ctrl.callsChartData = {
              series: [groupedCallsData],
              labels: labels
            }
        }

    }
})();
