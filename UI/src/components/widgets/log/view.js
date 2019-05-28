(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LogViewController', LogViewController)
        .controller('LogViewController', LogViewController);

    LogViewController.$inject = ['$q','$interval', '$scope','logRepoData', '$uibModal'];

    function LogViewController($q, $interval, $scope, logRepoData, $uibModal) {
        var ctrl = this;

        ctrl.combinedChartOptions = {
            plugins: [
                Chartist.plugins.gridBoundaries(),
                Chartist.plugins.lineAboveArea(),
                Chartist.plugins.tooltip(),
                Chartist.plugins.pointHalo(),
                Chartist.plugins.legend()
            ],
            showArea: false,
            lineSmooth: false,
            fullWidth: false,
            chartPadding: 14,
            axisX: {
                type: Chartist.FixedScaleAxis,
                divisor: 5,
                labelInterpolationFnc: function(value) {
                    return moment(value).format('HH:mm:ss');
                }
            },
            axisY: {
                labelInterpolationFnc: function(value) {
                    return value === 0 ? 0 : ((Math.round(value * 100) / 100) + '');
                }
            }
        };

        var logRequest = {
            componentId: $scope.widgetConfig.componentId,
            max:  $scope.widgetConfig.options.maxEntries
        };

        ctrl.load = function() {
            return $q.all[logRepoData.logDetails(logRequest).then(processLogResponse)]
        };

        $interval(function () {
            ctrl.load();
        }, 60000);

        function processLogResponse(response) {
            var logData = _.isEmpty(response.result) ? {} : response.result.content;
            ctrl.lastResult = logData[0];
            ctrl.title = ctrl.lastResult.name;

            ctrl.combinedChartData = {labels: [], series: []};

            var series = [];
            (logData).forEach(function (item) {
                var date = new Date(item.timestamp);
                ctrl.combinedChartData.labels.push('');
                item.metrics.forEach(function (metric) {
                    var values = series[metric.name];
                    if (null == values) {
                        values = [];
                        series[metric.name] = values;
                    }
                    values.push({x:date,y:metric.value});
                }, date);
            });

            Object.keys(series).forEach(function (value) {
                ctrl.combinedChartData.series.push({name:value,data:series[value]});
            });
        };
    }
})();
