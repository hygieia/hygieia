(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('performanceViewController', performanceViewController);

    performanceViewController.$inject = ['$q', '$scope','performanceData', '$modal', 'collectorData'];
    function performanceViewController($q, $scope, performanceData, $modal, collectorData) {
        var ctrl = this;

        ctrl.callsChartOptions = {
          plugins: [
            Chartist.plugins.gridBoundaries(),
            Chartist.plugins.lineAboveArea(),
            Chartist.plugins.pointHalo(),
            Chartist.plugins.ctPointClick({

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
          //low: 0,
          chartPadding: {
            right: 35,
            top: 20
          },
          showArea: true,
          lineSmooth: false,
          fullWidth: true,
          axisY: {
            allowDecimals: false,
            offset: 30,
            showGrid: true,
            showLabel: true,
            labelInterpolationFnc: function(value) {return Math.round(value * 100)/100;}
          }
        };

        ctrl.errorsChartOptions = {
          plugins: [
            Chartist.plugins.gridBoundaries(),
            Chartist.plugins.lineAboveArea(),
            Chartist.plugins.pointHalo(),
            Chartist.plugins.ctPointClick({

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
          //low: 0,
          chartPadding: {
            right: 35,
            top: 20
          },
          showArea: true,
          lineSmooth: false,
          fullWidth: true,
          axisY: {
            allowDecimals: false,
            offset: 30,
            showGrid: true,
            showLabel: true,
            labelInterpolationFnc: function(value) {return Math.round(value * 100)/100;}
          }
        };

        ctrl.pieOptions = {
          donut: true,
          donutWidth: 20,
          startAngle: 270,
          total: 200,
          showLabel: false
        };

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
                  ctrl.appID = element.options.appID;
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

        ctrl.showDetail = showDetail;

        function showDetail(evt){

          var pointIndex = evt;

              $modal.open({
                controller: 'PerformanceDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/performance/detail.html',
                size: 'lg',
                resolve: {
                  index: function(){
                    return evt;
                  },
                  warnings: function(){
                    return ctrl.warning;
                  },
                  good: function(){
                    return ctrl.good;
                  },
                  bad: function(){
                    return ctrl.bad;
                  }
                }
              });
        }

        function processResponse(data) {
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
            var calltimestamp = [];
            var errortimestamp = [];
            var healthruleviolations = [];
            var warnings = [];
            var good = [];
            var bad = [];

            _(data).sortBy('timeStamp').__wrapped__[0].metrics.forEach(function(innerelem){
              if (innerelem.name === 'Business Transaction Health Percent'){
                ctrl.businessavg = Math.round(innerelem.value*100 *10)/10;
              }
              if (innerelem.name === 'Node Health Percent'){
                ctrl.nodeavg = Math.round(innerelem.value*100 *10)/10;
              }
              if (innerelem.name === 'Error Rate Severity'){
                ctrl.errorvalue = innerelem.value;
              }
              if (innerelem.name === 'Response Time Severity'){
                ctrl.responsevalue = innerelem.value;
              }
              if (innerelem.name === 'Yolo JSON Object'){
                ctrl.violations = innerelem.value;
              }
            });

            ctrl.violations.forEach(function(element){
              if (element.severity === "WARNING"){
                if (element.incidentStatus === "OPEN") warnings.push(element);
                else good.push(element);
              }else {
                bad.push(element);
              }
            });

            ctrl.warning = warnings;
            ctrl.good = good;
            ctrl.bad = bad;

            _(data).sortBy('timeStamp').reverse().forEach(function(element){
              var metrictime = element.timestamp;
              var mins = (metrictime/60000) % 60;
              var hours = (((metrictime/60/60000) % 24) + 19) % 24;
              element.metrics.forEach(function(innerelem){
                if (innerelem.name === "Yolo JSON Object"){
                  healthruleviolations.push({
                    metrictime: metrictime,
                    value: innerelem.value});
                }
                if (innerelem.name === "Errors per Minute" && innerelem.value>0){
                  errorcount++;
                  errorspm += innerelem.value;
                  groupedErrorsData.push(innerelem.value);
                  errorlabels.push(Math.floor(hours) + ":" + Math.round(mins));
                  errortimestamp.push(metrictime);
                }
                if (innerelem.name === "Calls per Minute" && innerelem.value>0){
                  callcount++;
                  callspm += innerelem.value;
                  groupedCallsData.push(innerelem.value);
                  calllabels.push(Math.floor(hours) + ":" + Math.round(mins));
                  calltimestamp.push(metrictime);
                }
                if (innerelem.name === "Average Response Time (ms)" && innerelem.value>0){
                  responsecount++;
                  responsetime += innerelem.value;
                }
              });
            });
            ctrl.healthruleviolations = healthruleviolations.slice(healthruleviolations.length-7, healthruleviolations.length);
            ctrl.groupedCallsData = groupedCallsData;
            ctrl.groupedErrorsData = groupedErrorsData;
            ctrl.errorlabels = errorlabels;
            ctrl.calllabels = calllabels;
            ctrl.errortimestamp = errortimestamp;
            ctrl.calltimestamp = calltimestamp;


            if (errorcount!=0) errorspm = Math.round(errorspm/errorcount * 10)/10;
            else errorspm = 'No Data Collected';
            if (responsecount!=0) responsetime = Math.round(responsetime/responsecount * 10)/10;
            else responsetime = 'No Data Collected';
            if (callcount!=0) callspm = Math.round(callspm/callcount * 10)/10;
            else callspm = 'No Data Collected';

            ctrl.errorspm = errorspm;
            ctrl.callspm = callspm;
            ctrl.responsetime = responsetime;


            ctrl.transactionHealthData = {
              series: [ctrl.businessavg, 100-ctrl.businessavg]
            };

            ctrl.nodeHealthData = {
              series: [ctrl.nodeavg, 100-ctrl.nodeavg]
            };

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
