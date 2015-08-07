(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('cloudViewController', cloudViewController);

    cloudViewController.$inject = ['$scope', '$filter', 'cloudData', '$modal'];

    function cloudViewController($scope, $filter, cloudData, $modal) {
        // Set the current scope to ctrl. Then it can be referenced by other private functions
        // to set our public variables
        var ctrl = this;
        // public variables
        ctrl.componentId = $scope.widgetConfig.componentId;

        ctrl.detail = function (type) {
            $modal.open({
                templateUrl: 'components/widgets/cloud/detail.html',
                scope: $scope,
                controller: 'CloudWidgetDetailController',
                controllerAs: 'detail',
                resolve: {
                    getType: function () {
                        return type;
                    }
                }
            });
        };
        // public methods
        initializeDataStructures();
        configureOptions();

        var sortBy, direction;
        ctrl.load = function () {
            var params = {'componentId': ctrl.componentId}; //component id and filter from config
            //replace localTest with details
            cloudData.details(params).then(function (data) {
                ctrl.totalCount = data.totalInstanceCount;
                ctrl.stoppedCount = data.stoppedCount;

                ctrl.cpuData.series = [data.cpuLow, data.cpuMid, data.cpuHigh];
                ctrl.ageData.series = [data.ageGood, data.ageExpired, data.ageWarning];

                ctrl.notEncryptedCount = data.nonEncryptedCount;
                ctrl.notEncryptedPercent.series = [data.nonEncryptedCount / data.totalInstanceCount * 100, 100 - (data.nonEncryptedCount / data.totalInstanceCount * 100)];
                ctrl.notTaggedCount = data.nonTaggedCount;
                ctrl.notTaggedPercent.series = [data.nonTaggedCount / data.totalInstanceCount * 100, 100 - (data.nonTaggedCount / data.totalInstanceCount * 100)];
            }).catch(function (err) {
            });

            //replace localTest with table
            cloudData.table(params).then(function (data) {
                ctrl.tableData = orderBy(data, sortBy, direction);
            }).catch(function (err) {
            });

        }


        //template to sort table
        var orderBy = $filter('orderBy');
        $scope.order = function (predicate, reverse) {
            sortBy = predicate;
            direction = reverse;
            ctrl.tableData = orderBy(ctrl.tableData, predicate, reverse);
        };

        function initializeDataStructures() {
            ctrl.notTaggedPercent = {
                labels: [],
                series: [
                    []
                ]
            };
            ctrl.notEncryptedPercent = {
                labels: [],
                series: [
                    []
                ]
            };
            ctrl.ageData = {
                labels: ['pass', 'fail', 'warn'],
                series: []
            };
            ctrl.cpuData = {
                labels: ['low', 'high', 'normal'],
                series: [[]]
            };
        }


        function configureOptions() {

            ctrl.pieOptions = {
                //plugins available in recent chartist tooltip versions
                plugins: [
                    Chartist.plugins.tooltip({})],
                labelOverflow: true,
                labelOffset: 45,
                height: '250px',
                labelInterpolationFnc: function (value) {
                    return value
                }
            };
            ctrl.rainbowOptions = {
                donut: true,
                donutWidth: 20,
                startAngle: 270,
                total: 200,
                showLabel: false
            };


        }


    }
})();
