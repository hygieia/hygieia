(function() {
    'use strict';
    angular.module(HygieiaConfig.module)
        .controller('RallyViewController', RallyViewController);
    RallyViewController.$inject = ['$q', '$scope', 'rallyData', '$uibModal', 'collectorData'];

    function RallyViewController($q, $scope, rallyData, $uibModal, collectorData) {
        var ctrl = this;
        ctrl.showGraphDetail = showGraphDetail;
        ctrl.showRallyBuildDetail = showRallyBuildDetail;
        ctrl.projectTitle = $scope.widgetConfig.options.projectName;
        ctrl.lastUpdated = "";
        ctrl.load = function() {
            //start getting the response data using http service
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                projectId: $scope.widgetConfig.options.projectId,
            };
            rallyData.details(params)
                .then(function(data) {
                    processResponse(data);
                    if (data.length != 0) ctrl.lastUpdated = data[0].rallyFeature.lastUpdated;
                })
                .then(function() {
                    collectorData.getCollectorItem($scope.widgetConfig.componentId, 'AgileTool')
                        .then(function(data) {
                            deferred.resolve({
                                lastUpdated: ctrl.lastUpdated,
                                collectorItem: data
                            });
                        });
                });
            return deferred.promise;
            //end getting the response data using http service
        };

        function processResponse(data) {
            $scope.myInterval = 5000;
            $scope.noWrapSlides = false;
            $scope.active = 0;
            var currIndex = 0;
            $scope.rallyResults = [];
            var planEstimate, planEstimateArr, maxYaxisAccepted, labels, colors, data, options, datasetOverride, remainingDays, iterationName, totalUserStories, storyStages;
            _.each(data, function(item) {
                planEstimate = item.rallyFeature.planEstimate || 120;
                maxYaxisAccepted = (20 - (parseInt(planEstimate) % 20)) + parseInt(planEstimate);
                labels = item.rallyBurnDownData.iterationDates;
                colors = [];
                data = [];
                datasetOverride = [];
                remainingDays = item.rallyFeature.remainingDays;
                iterationName = item.rallyFeature.iterationName;
                totalUserStories = 0;
                storyStages = item.rallyFeature.storyStages;
                planEstimateArr = planEstimate;
                if (item.rallyFeature.storyStages.length > 0) {
                    totalUserStories = parseInt(item.rallyFeature.storyStages[0].backlog || 0) + parseInt(item.rallyFeature.storyStages[0].defined || 0) + parseInt(item.rallyFeature.storyStages[0].inProgress || 0) + parseInt(item.rallyFeature.storyStages[0].completed || 0) + parseInt(item.rallyFeature.storyStages[0].accepted || 0);
                }
                colors.push('#5c9acb', '#696969', '#7fb17f');
                data.push(item.rallyBurnDownData.toDoHours, item.rallyBurnDownData.totalTaskEstimate, item.rallyBurnDownData.acceptedPoints);
                options = {
                    scales: {
                        yAxes: [{
                            id: 'y-axis-1',
                            type: 'linear',
                            display: true,
                            position: 'right'
                        }, {
                            id: 'y-axis-1',
                            type: 'linear',
                            display: true,
                            position: 'left',
                            scaleLabel: {
                                display: true,
                                labelString: "Task To Do (Hours)"
                            },
                            ticks: {
                                beginAtZero: true,
                                min: 0
                            }
                        }, {
                            id: 'y-axis-2',
                            type: 'linear',
                            display: true,
                            position: 'right',
                            scaleLabel: {
                                display: true,
                                labelString: "Accepted (Points)"
                            },
                            ticks: {
                                beginAtZero: true,
                                min: 0,
                                max: maxYaxisAccepted,
                                stepSize: 20
                            }
                        }]
                    }
                };
                datasetOverride.push({
                    yAxisID: 'y-axis-1',
                    label: "Task To Do (Hours)",
                    type: 'bar',
                    backgroundColor: "rgba(92,154,203,1)"
                }, {
                    yAxisID: 'y-axis-1',
                    label: "Ideal (Hours)",
                    type: 'line',
                    borderColor: "rgba(105,105,105,1)",
                    fill: false
                }, {
                    yAxisID: 'y-axis-2',
                    label: "Accepted (Points)",
                    type: 'bar',
                    backgroundColor: "rgba(127,177,127,1)"
                });
                $scope.rallyResults.push({
                    "labels": labels,
                    "colors": colors,
                    "data": data,
                    "options": options,
                    "datasetOverride": datasetOverride,
                    "iterationName": iterationName,
                    "remainingDays": remainingDays,
                    "storyStages": storyStages,
                    "totalUserStories": totalUserStories,
                    "planEstimateArr": planEstimateArr,
                    "id": currIndex++
                });
            });
        }
        //To show the graph in detailed view
        function showGraphDetail(planEstimate, iterationBurnData, label) {
            $uibModal.open({
                controller: 'RallyGraphDetailController',
                controllerAs: 'graphdetail',
                templateUrl: 'components/widgets/rally/graphDetail.html',
                size: 'md',
                resolve: {
                    planEstimate: function() {
                        return parseInt(planEstimate)
                    },
                    iterationBurnData: function() {
                        return iterationBurnData;
                    },
                    label: function() {
                        return label;
                    }
                }
            });
        }
        //To show the userstroies involved in the current iteration
        function showRallyBuildDetail(data, status) {
            var resultData = [];
            resultData = data;
            switch (status) {
                case "Accepted":
                    resultData = _.filter(data, function(item) {
                        return item.state === "Accepted"
                    });
                    break;
                case "Completed":
                    resultData = _.filter(data, function(item) {
                        return item.state === "Completed"
                    });
                    break;
                case "In Progress":
                    resultData = _.filter(data, function(item) {
                        return item.state === "In Progress"
                    });
                    break;
                case "Defined":
                    resultData = _.filter(data, function(item) {
                        return item.state === "Defined"
                    });
                    break;
                default:
                    resultData = data;
            }
            $uibModal.open({
                controller: 'RallyBuildDetailController',
                controllerAs: 'rallyBuildDetail',
                templateUrl: 'components/widgets/rally/rallyBuildDetail.html',
                size: 'lg',
                resolve: {
                    resultData: function() {
                        return resultData;
                    }
                }
            });
        }
    }
})();