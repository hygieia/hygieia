(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CodeAnalysisViewController', CodeAnalysisViewController);

    CodeAnalysisViewController.$inject = ['$scope', 'codeAnalysisData', 'testSuiteData', 'libraryPolicyData', '$q', '$filter', '$uibModal', '$interval'];

    function CodeAnalysisViewController($scope, codeAnalysisData, testSuiteData, libraryPolicyData, $q, $filter, $uibModal, $interval) {
        var ctrl = this;

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };

        ctrl.minitabs = [
            {name: "Static Analysis"},
            {name: "Security"},
            {name: "OpenSource"},
            {name: "Tests"}

        ];

        ctrl.miniWidgetView = ctrl.minitabs[0].name;
        ctrl.miniToggleView = function (index) {
            ctrl.miniWidgetView = typeof ctrl.minitabs[index] === 'undefined' ? ctrl.minitabs[0].name : ctrl.minitabs[index].name;
        };

        ctrl.showLibraryPolicyDetails = showLibraryPolicyDetails;

        /** Auto Cycle variables */
        var timeoutPromise = null;
        ctrl.changeDetect = null;
        ctrl.pauseQualityView = pauseQualityView;
        ctrl.animateQualityView = animateQualityView;
        ctrl.pausePlaySymbol = "pause";

        coveragePieChart({});

        ctrl.load = function () {
            var caRequest = {
                componentId: $scope.widgetConfig.componentId,
                max: 1
            };
            var testRequest = {
                componentId: $scope.widgetConfig.componentId,
                types: ['Functional', 'Manual'],
                max: 1
            };
            var performanceRequest = {
                componentId: $scope.widgetConfig.componentId,
                types: ['Performance'],
                max: 1
            };
            var saRequest = {
                componentId: $scope.widgetConfig.componentId,
                max: 1
            };
            var libraryPolicyRequest = {
                componentId: $scope.widgetConfig.componentId,
                max: 1
            };

            return $q.all([
                libraryPolicyData.libraryPolicyDetails(libraryPolicyRequest).then(processLibraryPolicyResponse),
                codeAnalysisData.staticDetails(caRequest).then(processCaResponse),
                codeAnalysisData.securityDetails(saRequest).then(processSaResponse),
                testSuiteData.details(testRequest).then(processTestResponse),
                testSuiteData.details(performanceRequest).then(processPerfResponse)

            ]);
        };

        function processCaResponse(response) {
            var deferred = $q.defer();
            var caData = _.isEmpty(response.result) ? {} : response.result[0];

            ctrl.reportUrl = caData.url;
            ctrl.versionNumber = caData.version;

            ctrl.rulesCompliance = getMetric(caData.metrics, 'violations_density');
            ctrl.qualityGate = getMetric(caData.metrics, 'alert_status');

            ctrl.showQualityGate = angular.isUndefined(ctrl.rulesCompliance.value);

            ctrl.technicalDebt = getMetric(caData.metrics, 'sqale_index');

            ctrl.linesofCode = getMetric(caData.metrics, 'ncloc');

            ctrl.issues = [
                getMetric(caData.metrics, 'blocker_violations', 'Blocker'),
                getMetric(caData.metrics, 'critical_violations', 'Critical'),
                getMetric(caData.metrics, 'major_violations', 'Major'),
                getMetric(caData.metrics, 'violations', 'Issues')
            ];
            ctrl.unitTests = [
                getMetric(caData.metrics, 'test_success_density', 'Success'),
                getMetric(caData.metrics, 'test_failures', 'Failures'),
                getMetric(caData.metrics, 'test_errors', 'Errors'),
                getMetric(caData.metrics, 'tests', 'Tests')
            ];

            ctrl.lineCoverage = getMetric(caData.metrics, 'line_coverage');

            coveragePieChart(ctrl.lineCoverage);

            deferred.resolve(response.lastUpdated);
            return deferred.promise;
        }

        function processSaResponse(response) {
            var deferred = $q.defer();
            var saData = _.isEmpty(response.result) ? {} : response.result[0];

            ctrl.securityIssues = getSecurityMetricsData(saData);

            deferred.resolve(response.lastUpdated);
            return deferred.promise;
        }

        function processLibraryPolicyResponse(response) {
            if (response !== null) {
                var deferred = $q.defer();
                var libraryData = (response === null) || _.isEmpty(response.result) ? {} : response.result[0];
                ctrl.libraryPolicyDetails = libraryData;
                var totalComponentCount;
                var knownComponentCount;
                var calculatePercentage;
                var criticalCountPolicy;
                var severeCountPolicy;
                var moderateCountPolicy;
                var policyAffectedCount;
                if (libraryData.totalComponentCount !== null && libraryData.totalComponentCount !== undefined) {
                    totalComponentCount = libraryData.totalComponentCount;
                }
                if (libraryData.knownComponentCount !== null && libraryData.knownComponentCount !== undefined) {
                    knownComponentCount = libraryData.knownComponentCount;
                }
                if (knownComponentCount || totalComponentCount) {
                    if (isNaN(knownComponentCount / totalComponentCount)) {
                        calculatePercentage = 0;
                    } else {
                        calculatePercentage = knownComponentCount / totalComponentCount;
                    }
                }
                if (angular.isDefined(libraryData.policyAlert) && libraryData.policyAlert.length > 0) {
                    criticalCountPolicy = 0;
                    severeCountPolicy = 0;
                    moderateCountPolicy = 0;
                    policyAffectedCount = 0;

                    if (libraryData.policyAlert[0].policycriticalCount !== null && libraryData.policyAlert[0].policycriticalCount !== undefined) {
                        criticalCountPolicy = libraryData.policyAlert[0].policycriticalCount;
                    }
                    if (libraryData.policyAlert[0].policysevereCount !== null && libraryData.policyAlert[0].policysevereCount !== undefined) {
                        severeCountPolicy = libraryData.policyAlert[0].policysevereCount;
                    }
                    if (libraryData.policyAlert[0].polimoderateCount !== null && libraryData.policyAlert[0].polimoderateCount !== undefined) {
                        moderateCountPolicy = libraryData.policyAlert[0].polimoderateCount;
                    }
                    if (libraryData.policyAlert[0].policyAffectedCount !== null && libraryData.policyAlert[0].policyAffectedCount !== undefined) {
                        policyAffectedCount = libraryData.policyAlert[0].policyAffectedCount;
                    }
                }
                if (libraryData.threats) {
                    if (libraryData.threats.License) {
                        ctrl.libraryLicenseThreats = libraryData.threats.License;
                        ctrl.libraryLicenseThreatCount = getLevelCount(libraryData.threats.License)
                    }
                    if (libraryData.threats.Security) {
                        ctrl.librarySecurityThreats = libraryData.threats.Security;
                        ctrl.librarySecurityThreatCount = getLevelCount(libraryData.threats.Security)
                    }
                    ctrl.knownComponentCount = knownComponentCount;
                    ctrl.knownComponentCountPercentage = Math.round(calculatePercentage * 100);
                    ctrl.criticalCountPolicy = criticalCountPolicy;
                    ctrl.severeCountPolicy = severeCountPolicy;
                    ctrl.moderateCountPolicy = moderateCountPolicy;
                    ctrl.policyAffectedCompCount = policyAffectedCount;
                    ctrl.donutData = {
                        //labels: [90,10],
                        series: [ctrl.knownComponentCountPercentage, (100 - ctrl.knownComponentCountPercentage)]
                    };
                    ctrl.donutOptions = {
                        donut: true,
                        donutWidth: 6,
                        width: "60",
                        showLabel: false
                    };
                }
                deferred.resolve(response.lastUpdated);
                return deferred.promise;
            }
        }

        function getSecurityMetricsData(data) {
            var issues = [];
            var totalSize = _.isEmpty(data.metrics) ? 0 : data.metrics.length;
            for (var index = 0; index < totalSize; ++index) {
                issues.push({
                    name: data.metrics[index].name,
                    formattedValue: data.metrics[index].formattedValue,
                    status: data.metrics[index].status
                });
            }
            return issues;
        }

        function processPerfResponse(response) {
            var deferred = $q.defer();

            ctrl.perfTests = processTestResponseByType(response, "Performance");

            deferred.resolve(response.lastUpdated);
            return deferred.promise;
        }

        function processTestResponse(response) {
            var deferred = $q.defer();

            ctrl.functionalTests = processTestResponseByType(response, "Functional");

            deferred.resolve(response.lastUpdated);
            return deferred.promise;
        }

        function processTestResponseByType(response, type) {
            var result = [];
            var index;
            var totalSize = _.isEmpty(response.result) ? 0 : response.result.length;
            for (index = 0; index < totalSize; ++index) {

                var testResult = _.isEmpty(response.result) ? {testCapabilities: []} : response.result[index];
                var allZeros = {
                    failureCount: 0, successCount: 0, skippedCount: 0, totalCount: 0
                };
                // Aggregate the counts of all Functional test suites
                var aggregate = _.reduce(_.filter(testResult.testCapabilities, {type: type}), function (result, capability) {
                    //New calculation: 3/10/16 - Topo Pal
                    result.failureCount += capability.failedTestSuiteCount;
                    result.successCount += capability.successTestSuiteCount;
                    result.skippedCount += capability.skippedTestSuiteCount;
                    result.totalCount += capability.totalTestSuiteCount;

                    return result;
                }, allZeros);
                var passed = aggregate.successCount;
                var allPassed = aggregate.successCount === aggregate.totalCount;
                var success = allPassed ? 100 : ((passed / (aggregate.totalCount)) * 100);


                ctrl.executionId = _.isEmpty(response.result) ? "-" : response.result[index].executionId;
                result.push({
                    name: testResult.description,
                    totalCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.totalCount, 0),
                    successCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.successCount, 0),
                    failureCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.failureCount, 0),
                    skippedCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.skippedCount, 0),
                    successPercent: aggregate.totalCount === 0 ? '-' : $filter('number')(success, 0) + '%',
                    details: testResult
                });
            }
            return result;
        }

        function coveragePieChart(lineCoverage) {
            lineCoverage.value = lineCoverage.value || 0;

            ctrl.unitTestCoverageData = {
                series: [lineCoverage.value, (100 - lineCoverage.value)]
            };
        }

        function getLevelCount(threats) {
            var counts = {};
            var level;
            var total = 0;

            counts['Critical'] = 0;
            counts['High'] = 0;
            counts['Medium'] = 0;
            counts['Low'] = 0;

            for (var i = 0; i < threats.length; ++i) {
                level = threats[i].level;
                if (level != 'None') {
                    counts[level] = threats[i].count;
                    total += threats[i].count;
                }
            }

            return {counts: counts, total: total};
        };

        function getMetric(metrics, metricName, title) {
            title = title || metricName;
            return angular.extend((_.find(metrics, {name: metricName}) || {name: title}), {name: title});
        }

        function calculateTechnicalDebt(value) {
            var factor, suffix;
            if (!value) return '-';
            if (value < 1440) {
                // hours
                factor = 60;
                suffix = 'h';
            } else if (value < 525600) {
                // days
                factor = 1440;
                suffix = 'd';
            } else {
                // years
                factor = 525600;
                suffix = 'y';
            }
            return Math.ceil(value / factor) + suffix;
        }

        ctrl.getDashStatus = function getDashStatus(level) {
            if (level == undefined) return 'ignore';
            switch (level.toLowerCase()) {
                case 'critical':
                    return 'critical';

                case 'high':
                    return 'alert';

                case 'medium':
                    return 'warning';

                case 'low':
                    return 'ignore';

                default:
                    return 'ok';
            }
        }

        function showLibraryPolicyDetails(type, data) {
            $uibModal.open({
                controller: 'LibraryPolicyDetailsController',
                controllerAs: 'libraryPolicyDetails',
                templateUrl: 'components/widgets/codeanalysis/librarypolicydetails.html',
                size: 'lg',
                resolve: {
                    libraryPolicyResult: function () {
                        return ({type: type, data: data});
                    }
                }
            });
        }

        /**
         * Changes timeout boolean to know whether or not to count down
         */
        ctrl.startTimeout = function () {
            ctrl.stopTimeout();

            timeoutPromise = $interval(function () {
                animateQualityView(false);
            }, 7000);
        }

        /**
         * Stops the current cycle promise
         */
        ctrl.stopTimeout = function () {
            $interval.cancel(timeoutPromise);
        };

        /**
         * Starts timeout cycle function by default
         */
        ctrl.startTimeout();

        /**
         * Animates quality view switching
         */
        function animateQualityView(resetTimer) {
            // update the selected view
            var currentIndex = ctrl.minitabs.findIndex(x => x.name==ctrl.miniWidgetView);
            var newIndex = currentIndex + 1;

            if (newIndex >= ctrl.minitabs.length){
                ctrl.miniWidgetView = ctrl.minitabs[0].name;
            } else {
                ctrl.miniWidgetView = ctrl.minitabs[newIndex].name;
            }

            if (resetTimer && timeoutPromise.$$state.value != "canceled") {
                ctrl.stopTimeout();
                ctrl.startTimeout();
            }
        }

        /**
         * Pauses quality view switching via manual button from user interaction
         */
        function pauseQualityView() {
            if (timeoutPromise.$$state.value === "canceled") {
                ctrl.pausePlaySymbol = "pause";
                ctrl.startTimeout();
            } else {
                ctrl.pausePlaySymbol = "play";
                ctrl.stopTimeout();
            }
        };
    }
})
();
