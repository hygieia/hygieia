(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productQualityDetailsController', productQualityDetailsController);

    productQualityDetailsController.$inject = ['modalData', '$uibModalInstance', '$timeout'];
    function productQualityDetailsController(modalData, $uibModalInstance, $timeout) {
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

        function getDateLabels(count, noGaps) {
            var labels = [];
            for(var x = count; x > 0; x--) {
                if(x % 7 == 0) {
                    labels.push(moment().add(-1 * x, 'days').format('MMM DD'));
                }
                else if (!noGaps) {
                    labels.push('');
                }
            }

            return labels;
        }

        // set some basic options so we're not stuck copying them everywhere
        function getDefaultChartOptions(yAxisTitle) {
            return {
                showArea: false,
                lineSmooth: Chartist.Interpolation.none({
                    fillHoles: true
                }),
                fullWidth: true,
                chartPadding: { top: 10, right: 10, bottom: 10, left: 20 },
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
                },
                plugins: [
                    Chartist.plugins.ctAxisTitle({
                        axisY: {
                            axisTitle: yAxisTitle,
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 20
                            },
                            textAnchor: 'middle',
                            flipTitle: true
                        }
                    })
                ]
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
                    var avg = tests.length ? _(tests).map(metric).reduce(function(a, b) { return a + b; }) / tests.length : 0;

                    // set obj
                    return [parseInt(key), avg];
                }).forEach(function(item) {
                // populate an object instead of dealing with arrays so
                // we can get to it by property names
                rawData[item[0]] = item[1];
            });

            for(var x = -90; x < 0; x++) {
                var existingValue = rawData[x];

                data.push({value: existingValue == undefined && defaultValue != undefined ? defaultValue : rawData[x]});
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

            var options = getDefaultChartOptions('% unit tests passed per day');
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

            ctrl.codeCoverageChartOptions = getDefaultChartOptions('# of code issues per day');
        }

        function prepareCodeIssuesChartData()
        {
            var violations = getCodeAnalysisData('violations', 0),
                critical = getCodeAnalysisData('criticalViolations', 0),
                major = getCodeAnalysisData('majorViolations', 0),
                blocker = getCodeAnalysisData('blockerViolations', 0);

            _(blocker.data).forEach(function(record, idx) {
                // set a custom tooltip
                var tip = '';
                if(violations.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(violations.data[idx].value) + ' issues</div>';
                }

                if(major.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(major.data[idx].value) + ' major issues</div>';
                }

                if(blocker.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(blocker.data[idx].value) + ' blocking issues</div>';
                }

                if(critical.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(critical.data[idx].value) + ' critical issues</div>';
                }

                if(tip.length) {
                    violations.data[idx].meta = tip;
                    major.data[idx].meta = tip;
                    blocker.data[idx].meta = tip;
                    critical.data[idx].meta = tip;
                }
            });

            ctrl.codeIssuesChartData = {
                labels: violations.labels,
                series: [
                    violations.data,
                    major.data,
                    blocker.data,
                    critical.data
                ]
            };

            var options = getDefaultChartOptions('Avg. code issues per day');
            options.stackBars = true;
            options.plugins.push(Chartist.plugins.tooltip());

            ctrl.codeIssuesChartOptions = options;
        }

        function prepareSecurityAnalysisChartData()
        {
            var issuesData = [],
                blocker = getSecurityAnalysisData('blocker', 0),
                critical = getSecurityAnalysisData('critical', 0),
                major = getSecurityAnalysisData('major', 0);

            // create an empty issues data to preserve series colors across charts
            _(blocker.data).forEach(function(record, idx) {
                issuesData.push({value: 0});

                // set a custom tooltip
                var tip = '';
                if(major.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(major.data[idx].value) + ' major issues</div>';
                }

                if(blocker.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(blocker.data[idx].value) + ' blocking issues</div>';
                }

                if(critical.data[idx].value > 0) {
                    tip += '<div class="tooltip-row">' + Math.ceil(critical.data[idx].value) + ' critical issues</div>';
                }

                if(tip.length) {
                    major.data[idx].meta = tip;
                    blocker.data[idx].meta = tip;
                    critical.data[idx].meta = tip;
                }
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

            var options = getDefaultChartOptions('# of security issues');
            options.plugins.push(Chartist.plugins.tooltip());
            options.stackBars = true;
            ctrl.securityAnalysisChartOptions = options;
        }

        function prepareBuildSuccessChartData() {
            var rawData = {},
                data = [];

            // build success is being passed as an already grouped value
            _(modalData.team.data.buildSuccess)
                .forEach(function(item) {
                    // populate an object instead of dealing with arrays so
                    // we can get to it by property names
                    rawData[item[0]] = parseFloat((item[1] * 100).toFixed(1));
                });

            for(var x = -90; x < 0; x++) {
                data.push(rawData[x]);
            }

            ctrl.buildSuccessChartData = {
                labels: getDateLabels(data.length),
                series: [{
                    name: 'Build Success',
                    data: data
                }]
            };

            var options = getDefaultChartOptions('% build success');
            ctrl.buildSuccessChartOptions = options;
        }

        function prepareFixedBuildChartData()
        {
            var data = _(modalData.team.data.fixedBuild).map(function(record) {
                var timeToFixInMinutes = moment.duration(moment(record.fixedBuild.timestamp).diff(record.brokenBuild.timestamp)).asMinutes()

                return {
                    meta: (function(r) {
                        return '<div class="tooltip-row tooltip-left">#' + r.brokenBuild.number + ' broke on '
                            + moment(r.brokenBuild.timestamp).format('MM/DD/YY [at] hh:mm A')
                            + '</div><div class="tooltip-row tooltip-left">#' + r.fixedBuild.number + ' fixed on '
                            + moment(r.fixedBuild.timestamp).format('MM/DD/YY [at] hh:mm A') + '</div>';
                    })(record),
                    value: {
                        x: daysAgo(record.fixedBuild.timestamp),
                        y: timeToFixInMinutes
                    }
                };
            }).value();

            ctrl.fixedBuildChartData = {
                series: [
                    data
                ]
            };

            var options = getDefaultChartOptions('Minutes to fix build');
            options.plugins.push(Chartist.plugins.gridBoundaries());
            options.plugins.push(Chartist.plugins.tooltip({ className: 'fixed-build-tooltip' }));
            options.plugins.push(Chartist.plugins.axisLabels({
                        axisX: {
                            labels: getDateLabels(90, true)
                        }
                    }));

            options.axisX = {
                type: Chartist.AutoScaleAxis,
                onlyInteger: true,
                showLabel: false,
                low: -90,
                high: 0
            };
            options.showLine = false;

            ctrl.fixedBuildChartOptions = options;
        }

        function prepareFuncTestsPassedData()
        {
            var series = [];
            _(modalData.team.data.testSuite)
                .groupBy('collectorItemId')
                .forEach(function(tests, key) {
                    var rawData = {},
                        name = tests[0].name;

                    _(tests).groupBy(function(test) {
                        return daysAgo(test.timestamp);
                    }).forEach(function(tests, key) {
                        var passed = _(tests).map('successCount').reduce(function(a, b) { return a + b; }),
                            total = _(tests).map('totalCount').reduce(function(a, b) { return a + b; }),
                            tip = '';

                        _(tests).forEach(function(run) {
                            tip += '<div class="tooltip-row">' + run.successCount + ' of ' + run.totalCount + ' tests passed on ' + moment(run.timestamp).format('MM/DD/YY [at] hh:mm A') + '</div>';
                        });


                        rawData[parseInt(key)] = {tip: tip, percentPassed: total ? parseFloat((passed / total * 100).toFixed(1)) : 0};
                    });

                    var data = [];
                    for(var x = -90; x<0; x++) {
                        if(rawData[x] != undefined) {
                            var obj = rawData[x];
                            var tip = '<div class="modal-label">' + name + '</div>' + obj.tip;

                            data.push({
                                meta: tip,
                                value: obj.percentPassed
                            });
                        }
                        else {
                            data.push({value:null});
                        }
                    }

                    series.push({
                        name: name,
                        data: data
                    });
                });

            ctrl.funcTestsPassedChartData = {
                labels: getDateLabels(series[0].data.length),
                series: series
            };

            var options = getDefaultChartOptions('% of tests passed by suite');
            options.plugins.push(Chartist.plugins.tooltip());

            ctrl.funcTestsPassedChartOptions = options;

            var seriesChartNames = ['a','b','c','d'];
            ctrl.funcTestsPassedLegend = _(series).map(function(dataset, idx) {
                return {
                    name: dataset.name,
                    chartSeriesName: seriesChartNames[idx % seriesChartNames.length]
                }
            }).value();
        }
    }
})();