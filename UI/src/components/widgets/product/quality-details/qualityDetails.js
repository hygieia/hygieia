(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productQualityDetailsController', productQualityDetailsController);

    productQualityDetailsController.$inject = ['modalData', '$modalInstance', '$timeout'];
    function productQualityDetailsController(modalData, $modalInstance, $timeout) {
        /*jshint validthis:true */
        var ctrl = this;
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

        ctrl.selectTab = function(idx) {
            var fn = false;
            switch(idx) {
                case 0:
                    if(!ctrl.unitTestChartData)
                    {
                        fn = prepareUnitTestChartData;
                    }
                    break;
                case 1:
                    if(!ctrl.codeCoverageChartData) {
                        fn = prepareCodeCoverageChartData;
                    }
                    break;
                case 2:
                    if(!ctrl.codeIssuesChartData) {
                        fn = prepareCodeIssuesChartData;
                    }
                    break;
                case 3:
                    if(!ctrl.securityAnalysisChartData) {
                        fn = prepareSecurityAnalysisChartData;
                    }
                    break;
                case 4:
                    if(!ctrl.buildSuccessChartData) {
                        fn = prepareBuildSuccessChartData;
                    }
                    break;
                case 5:
                    if(!ctrl.fixedBuildChartData) {
                        fn = prepareFixedBuildChartData;
                    }
                    break;
                case 6:
                    if(!ctrl.funcTestsPassedChartData) {
                        fn = prepareFuncTestsPassedData;
                    }
                    break;
                default:
                    break;
            }

            if(fn) {
                $timeout(fn, 50);
            }
        };

        function getDateLabels(count) {
            var labels = [];
            for(var x = count; x > 0; x--) {
                var text = '';
                if(x % 7 == 0) {
                    labels.push(moment().add(-1 * x, 'days').format('MMM DD'));
                }

                labels.push(text);
            }

            return labels;
        }

        // set some basic options so we're not stuck copying them everywhere
        function getDefaultChartOptions() {
            return {
                plugins: [
                    Chartist.plugins.gridBoundaries()
                ],
                showArea: false,
                lineSmooth: Chartist.Interpolation.none({
                    fillHoles: true
                }),
                fullWidth: true,
                chartPadding: 7,
                axisX: {
                    //showLabel: false
                },
                axisY: {
                    labelOffset: {
                        x: 0,
                        y: 5
                    },
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
                var existingValue = rawData[x];

                data.push(existingValue == undefined && defaultValue != undefined ? defaultValue : rawData[x]);
            }

            return {
                labels: getDateLabels(data.length),
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

            var options = getDefaultChartOptions();
            //options.low = 0;
            //options.high = 100;
            ctrl.unitTestChartOptions = options;
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

            ctrl.codeCoverageChartOptions = getDefaultChartOptions();
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
                    rawData[item[0]] = parseFloat((item[1] * 100).toFixed(1));
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

            ctrl.buildSuccessChartOptions = getDefaultChartOptions();
        }

        function prepareFixedBuildChartData()
        {
            var data = _(modalData.team.data.fixedBuild).map(function(record) {
                return { x: record[0], y: parseFloat(record[1].toFixed(1)) };
            }).value();

            ctrl.fixedBuildChartData = {
                series: [
                    data
                ]
            };

            var options = angular.extend(getDefaultChartOptions(), {
                plugins: [
                    Chartist.plugins.gridBoundaries()
                ],
                axisX: {
                    type: Chartist.AutoScaleAxis,
                    onlyInteger: true,
                    showLabel: false
                },
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
            });

            ctrl.funcTestsPassedChartData = {
                labels: labels,
                series: series//[series[0].data]
            };

            var options = getDefaultChartOptions();
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