(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('performanceViewController', performanceViewController);

    performanceViewController.$inject = ['$q', '$scope','performanceData', '$modal', 'collectorData'];
    function performanceViewController($q, $scope, performanceData, $modal, collectorData) {
        var ctrl = this;

        ctrl.genericChartOptions = {
          plugins: [
            Chartist.plugins.gridBoundaries(),
            Chartist.plugins.lineAboveArea(),
            Chartist.plugins.pointHalo(),
            Chartist.plugins.ctPointClick({
              //TODO
            }),
            Chartist.plugins.ctAxisTitle({
              axisX: {
                axisTitle: 'Timestamp',
                axisClass: 'ct-axis-title',
                offset: {
                  x: 0,
                  y: 50
                },
                textAnchor: 'middle'
              }
            }),
            Chartist.plugins.ctPointLabels({
              textAnchor: 'middle'
            })
          ],
          low: 0,
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

        ctrl.calls = 100;

        ctrl.pieOptions = {
          donut: true,
          donutWidth: 20,
          startAngle: 270,
          total: 200,
          showLabel: false
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
        //ctrl.showDetail = showDetail;
        ctrl.load = function() {

            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
            };

            console.log($scope.widgetConfig.componentId);
            collectorData.itemsByType('appPerformance').then(function(data){
              data.forEach(function(element){
                if (element.enabled)
                  ctrl.appname = element.description;
              });
            });

            console.log("checkpoint1");
            performanceData.appPerformance({componentId: $scope.widgetConfig.componentId}).then(function(data) {
                console.log("checkpoint2");
                console.log("widget component id: " + $scope.widgetConfig.componentId);
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });

            console.log("checkpoint3");
            return deferred.promise;
        };

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
            var groupedErrorsData = [];
            var calllabels = [];
            var errorlabels = [];
            var errorcount = 0;
            var callcount = 0;
            var responsecount = 0;
            var nodehealth = 0;
            var businesshealth = 0;
            var errorspm = 0;
            var callspm = 0;
            var responsetime = 0;

            _(data).sortBy('timeStamp').reverse().forEach(function(element){
              var metrictime = element.timestamp;
              var mins = (metrictime/60000) % 60;
              var hours = (((metrictime/60/60000) % 24) + 19) % 24;
              element.metrics.forEach(function(innerelem){
                if (innerelem.name === "Errors per Minute" && innerelem.value>0){
                  errorcount++;
                  errorspm += innerelem.value;
                  groupedErrorsData.push(innerelem.value);
                  errorlabels.push(Math.round(hours) + ":" + Math.round(mins));
                }
                if (innerelem.name === "Calls per Minute" && innerelem.value>0){
                  callcount++;
                  callspm += innerelem.value;
                  groupedCallsData.push(innerelem.value);
                  calllabels.push(Math.round(hours) + ":" + Math.round(mins));
                }
                if (innerelem.name === "Average Response Time (ms)" && innerelem.value>0){
                  responsecount++;
                  responsetime += innerelem.value;
                }
              });
            });

            console.log(groupedCallsData);
            console.log(calllabels);

            /*
            _(data).sortBy('timeStamp').reverse().slice(0, 15).forEach(function(element){
                element.metrics.forEach(function(innerelem2){
                  if (innerelem2.name === "Calls per Minute")
                    groupedCallsData.push(innerelem2.value);
                  if (innerelem2.name === "Errors per Minute")
                    groupedErrorsData.push(innerelem2.value);

                });
                labels.push('');
            });*/

            errorspm = Math.round(errorspm/errorcount * 10)/10;
            callspm = Math.round(callspm/callcount * 10)/10;
            responsetime = Math.round(responsetime/responsecount * 10)/10;
            ctrl.errorspm = errorspm;
            ctrl.callspm = callspm;
            ctrl.responsetime = responsetime;
            //console.log(groupedCallsData);
            //console.log(labels);
            /*var nodehealthavg = Math.round(nodehealth/count * 10)/10;
            console.log("nodehealth: " + nodehealthavg);
            var businesshealthavg = Math.round(businesshealth/count * 10)/10;
            ctrl.businessavg = businesshealthavg;
            ctrl.nodeavg = nodehealthavg;
            ctrl.transactionHealthData = {
              series: [businesshealthavg, 100-businesshealthavg]
            };

            ctrl.nodeHealthData = {
              series: [nodehealthavg, 100-nodehealthavg]
            };*/
            ctrl.callsChartData = {
              series: [groupedCallsData.slice(groupedCallsData.length-7, groupedCallsData.length)],
              labels: calllabels.slice(calllabels.length-7, calllabels.length)
            };

            ctrl.errorsChartData = {
              series: [groupedErrorsData.slice(groupedErrorsData.length-7, groupedErrorsData.length)],
              labels: errorlabels.slice(errorlabels.length-7, errorlabels.length)
            };
        }

    }
})();
