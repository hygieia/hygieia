(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LogViewController', LogViewController)
        .controller('LogViewController', LogViewController);

    LogViewController.$inject = ['$q', '$scope','logRepoData', 'collectorData', '$uibModal'];

    function LogViewController($q, $scope, logRepoData, collectorData, $uibModal) {
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
            lineSmooth: true,
            fullWidth: false,
            chartPadding: 7,
            axisX: {
                showLabel: true
            },
            axisY: {
                labelInterpolationFnc: function(value) {
                    return value === 0 ? 0 : ((Math.round(value * 100) / 100) + '');
                }
            }
        };

        var logRequest = {
            componentId: $scope.widgetConfig.componentId,
            max: 100
        };

        ctrl.load = function() {
            return $q.all[logRepoData.logDetails(logRequest).then(processLogResponse)]
        };

        function processLogResponse(response) {
            var deferred = $q.defer();
            var logData = _.isEmpty(response.result) ? {} : response.result;
            ctrl.lastResult = logData[0];
            ctrl.title = ctrl.lastResult.name;

            ctrl.combinedChartData = {labels: [], series: []};

            var series = [];
            (logData).forEach(function (item) {
                item.metrics.forEach(function (metric) {
                    var values = series[metric.name];
                    if (null == values) {
                        values = [];
                        series[metric.name] = values;
                    }
                    values.push(metric.value);
                });
            });

            Object.keys(series).forEach(function (value) {
                console.log(value);
                ctrl.combinedChartData.series.push({name:value,data:series[value]});
                ctrl.combinedChartData.labels.push('');
            });
        };
    }
})();