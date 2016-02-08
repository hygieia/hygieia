(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productQualityDetailsController', productQualityDetailsController);

    productQualityDetailsController.$inject = ['modalData', '$modalInstance', '$timeout'];
    function productQualityDetailsController(modalData, $modalInstance, $timeout) {
        /*jshint validthis:true */
        var ctrl = this;
modalData.metricIndex = 6;
        ctrl.tabSettings = {
            unitTests: { active: modalData.metricIndex == 0},
            codeCoverage: { active: modalData.metricIndex == 1},
            codeIssues: { active: modalData.metricIndex == 2},
            securityIssues: { active: modalData.metricIndex == 3},
            buildSuccess: { active: modalData.metricIndex == 4},
            buildFix: { active: modalData.metricIndex == 5},
            functionalTestsPassed: { active: modalData.metricIndex == 6}
        };

        ctrl.initTabIndex = modalData.metricIndex;
        ctrl.unitTests = modalData.team.summary.unitTests;
        ctrl.codeCoverage = modalData.team.summary.codeCoverage;
        ctrl.codeIssues = modalData.team.summary.codeIssues;
        ctrl.securityIssues = modalData.team.summary.securityIssues;
        ctrl.buildSuccess = modalData.team.summary.buildSuccess;
        ctrl.buildFix = modalData.team.summary.buildFix;
        ctrl.functionalTestsPassed = modalData.team.summary.functionalTestsPassed;

        prepareUnitTestChartData();
        prepareCodeCoverageChartData();
        prepareCodeIssuesChartData();
        prepareSecurityAnalysisChartData();
        prepareBuildSuccessChartData();
        prepareFixedBuildChartData();
        prepareFuncTestsPassedData();

        // set some basic options so we're not stuck copying them everywhere
        function getLineGraphOptions() {
            return {
                plugins: [
                    Chartist.plugins.gridBoundaries()
                ],
                showArea: true,
                lineSmooth: false,
                fullWidth: true,
                chartPadding: 7,
                axisX: {
                    showLabel: false
                },
                axisY: {
                    labelInterpolationFnc: function(value) {
                        return value === 0 ? 0 : ((Math.round(value * 100) / 100) + '');
                    }
                }
            };
        }

        function daysAgo(timestamp) {
            return -1 * Math.floor(moment.duration(moment().diff(moment(timestamp).startOf('day').valueOf())).asDays())
        }

        function getCodeAnalysisData(metric, defaultValue) {
            return getAnalysisData(modalData.team.data.codeAnalysis, metric, defaultValue);
        }

        function getSecurityAnalysisData(metric, defaultValue) {
            return getAnalysisData(modalData.team.data.securityAnalysis, metric, defaultValue);
        }

        function getAnalysisData(origData, metric, defaultValue) {
            var data = [],
                labels = [],
                rawData = {};

            _(origData)
                .groupBy(function(row) {
                    return daysAgo(row.timestamp);
                })
                .map(function(tests, key) {
                    var avg = tests.length ? _(tests).pluck(metric).reduce(function(a, b) { return a + b; }) / tests.length : 0;

                    // set obj
                    return [parseInt(key), avg];
                }).forEach(function(item) {
                // populate an object instead of dealing with arrays so
                // we can get to it by property names
                rawData[item[0]] = item[1];
            });

            for(var x = -90; x < 0; x++) {
                labels.push('');
                var existingValue = rawData[x];

                data.push(existingValue == undefined && defaultValue != undefined ? defaultValue : rawData[x]);
            }

            return {
                labels: labels,
                data: data
            }
        }

        function prepareUnitTestChartData()
        {
            var data = getCodeAnalysisData('testSuccessDensity');

            ctrl.unitTestChartData = {
                labels: data.labels,
                series: [{
                    name: 'Unit test success',
                    data: data.data
                }]
            };

            ctrl.unitTestChartOptions = getLineGraphOptions();
        }

        function prepareCodeCoverageChartData()
        {
            var data = getCodeAnalysisData('lineCoverage');

            ctrl.codeCoverageChartData = {
                labels: data.labels,
                series: [{
                    name: 'Line Coverage',
                    data: data.data
                }]
            };

            ctrl.codeCoverageChartOptions = getLineGraphOptions();
        }

        function prepareCodeIssuesChartData()
        {
            var violations = getCodeAnalysisData('violations', 0),
                critical = getCodeAnalysisData('criticalViolations', 0),
                major = getCodeAnalysisData('majorViolations', 0),
                blocker = getCodeAnalysisData('blockerViolations', 0);

            ctrl.codeIssuesChartData = {
                labels: violations.labels,
                series: [
                    violations.data,
                    major.data,
                    blocker.data,
                    critical.data
                ]
            };

            ctrl.codeIssuesChartOptions = {
                stackBars: true,
                fullWidth: true
            }
        }

        function prepareSecurityAnalysisChartData()
        {
            var issuesData = [],
                blocker = getSecurityAnalysisData('blocker', 0),
                critical = getSecurityAnalysisData('critical', 0),
                major = getSecurityAnalysisData('major', 0);

            // create an empty issues data to preserve series colors across charts
            _(blocker.data).forEach(function() {
                issuesData.push(0);
            });

            ctrl.securityAnalysisChartData = {
                labels: blocker.labels,
                series: [
                    issuesData,
                    major.data,
                    blocker.data,
                    critical.data
                ]
            };

            ctrl.securityAnalysisChartOptions = {
                stackBars: true,
                fullWidth: true
            }
        }

        function prepareBuildSuccessChartData() {
            var rawData = {},
                labels = [],
                data = [];

            // build success is being passed as an already grouped value
            _(modalData.team.data.buildSuccess)
                .forEach(function(item) {
                    // populate an object instead of dealing with arrays so
                    // we can get to it by property names
                    rawData[item[0]] = item[1];
                });

            for(var x = -90; x < 0; x++) {
                labels.push('');
                data.push(rawData[x]);
            }

            ctrl.buildSuccessChartData = {
                labels: labels,
                series: [{
                    name: 'Build Success',
                    data: data
                }]
            };

            ctrl.buildSuccessChartOptions = getLineGraphOptions();
        }

        function prepareFixedBuildChartData()
        {
            var data = _(modalData.team.data.fixedBuild).map(function(record) {
                return { x: record[0], y: record[1] };
            }).value();

            ctrl.fixedBuildChartData = {
                series: [
                    data
                ]
            };

            var options = angular.extend(getLineGraphOptions(), {
                axisX: {
                    type: Chartist.AutoScaleAxis,
                    onlyInteger: true,
                    showLabel: false
                },
                showArea: false,
                showLine: false
            });

            ctrl.fixedBuildChartOptions = options;
        }

        function prepareFuncTestsPassedData()
        {
            var labels = [],
                series = [];
            _(modalData.team.data.testSuite)
                .groupBy('collectorItemId')
                .forEach(function(tests, key) {
                    var rawData = {},
                        name = tests[0].name;

                    _(tests).groupBy(function(test) {
                        return daysAgo(test.timestamp);
                    }).forEach(function(tests, key) {
                        var passed = _(tests).pluck('successCount').reduce(function(a, b) { return a + b; }),
                            total = _(tests).pluck('totalCount').reduce(function(a, b) { return a + b; });


                        rawData[parseInt(key)] = total ? parseFloat((passed / total * 100).toFixed(1)) : 0;
                    });

                    var data = [];
                    for(var x = -90; x<0; x++) {
                        data.push(rawData[x]);
                    }

                    series.push({
                        name: name,
                        data: data
                    });
                });

            _(series[0].data).forEach(function() {
                labels.push('');
            })

            ctrl.funcTestsPassedChartData = {
                labels: labels,
                series: series//[series[0].data]
            };

            var options = getLineGraphOptions();
            //var options = angular.extend(getLineGraphOptions(), {
            //    axisX: {
            //        type: Chartist.AutoScaleAxis,
            //        onlyInteger: true,
            //        showLabel: false
            //    }
            //});
            ctrl.funcTestsPassedChartOptions = options;

            var seriesChartNames = ['a','b','c','d'];
            ctrl.funcTestsPassedLegend = _(series).map(function(dataset, idx) {
                return {
                    name: dataset.name,
                    chartSeriesName: seriesChartNames[idx % seriesChartNames.length]
                }
            }).value();
            console.log(ctrl.funcTestsPassedLegend);
        }
    }
})();