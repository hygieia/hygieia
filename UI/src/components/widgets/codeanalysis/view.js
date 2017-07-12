(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CodeAnalysisViewController', CodeAnalysisViewController);

    CodeAnalysisViewController.$inject = ['$scope', 'codeAnalysisData', 'testSuiteData', 'libraryPolicyData', '$q', '$filter', '$uibModal'];
    function CodeAnalysisViewController($scope, codeAnalysisData, testSuiteData, libraryPolicyData, $q, $filter, $uibModal) {
        var ctrl = this;

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };

        ctrl.showStatusIcon = showStatusIcon;
        ctrl.showDetail = showDetail;
        ctrl.showLibraryPolicyDetails = showLibraryPolicyDetails;

        coveragePieChart({});

        ctrl.load = function () {
            var caRequest = {
                componentId: $scope.widgetConfig.componentId,
                max: 1
            };
            var testRequest = {
                componentId: $scope.widgetConfig.componentId,
                types: ['Functional'],
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
                testSuiteData.details(testRequest).then(processTestResponse)

            ]);
        };

        function processCaResponse(response) {
            var deferred = $q.defer();
            var caData = _.isEmpty(response.result) ? {} : response.result[0];


            ctrl.reportUrl = response.reportUrl;
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
                if (libraryData.threats) {
                    if (libraryData.threats.License) {
                        ctrl.libraryLicenseThreats = libraryData.threats.License;
                        ctrl.libraryLicenseThreatStatus = getLibraryPolicyStatus(libraryData.threats.License)
                    }
                    if (libraryData.threats.Security) {
                        ctrl.librarySecurityThreats = libraryData.threats.Security;
                        ctrl.librarySecurityThreatStatus = getLibraryPolicyStatus(libraryData.threats.Security)
                    }
                }
                deferred.resolve(response.lastUpdated);
                return deferred.promise;
            }
        }

            function getSecurityMetricsData (data) {
                var issues = [];
                var totalSize = _.isEmpty(data.metrics) ? 0 : data.metrics.length;
                for (var index = 0; index < totalSize; ++index) {
                    issues.push({name: data.metrics[index].name, formattedValue : data.metrics[index].formattedValue, status:data.metrics[index].status});
                }
                return issues;
            }


            function processTestResponse(response) {
                var deferred = $q.defer();

                ctrl.testResult = testResult;

                ctrl.functionalTests = [];
                var index;
                var totalSize = _.isEmpty(response.result) ? 0 : response.result.length;
                for (index = 0; index < totalSize; ++index) {

                    var testResult = _.isEmpty(response.result) ? {testCapabilities: []} : response.result[index];
                    var allZeros = {
                        failureCount: 0, successCount: 0, skippedCount: 0, totalCount: 0
                    };
                    // Aggregate the counts of all Functional test suites
                    var aggregate = _.reduce(_.filter(testResult.testCapabilities, {type: "Functional"}), function (result, capability) {
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
                    ctrl.functionalTests.push({
                        name: $scope.widgetConfig.options.testJobNames[index],
                        totalCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.totalCount, 0),
                        successCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.successCount, 0),
                        failureCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.failureCount, 0),
                        skippedCount: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.skippedCount, 0),
                        successPercent: aggregate.totalCount === 0 ? '-' : $filter('number')(success, 0) + '%',
                        details: testResult
                    });
                }
                deferred.resolve(response.lastUpdated);
                return deferred.promise;
            }

            function coveragePieChart(lineCoverage) {
                lineCoverage.value = lineCoverage.value || 0;

                ctrl.unitTestCoverageData = {
                    series: [lineCoverage.value, (100 - lineCoverage.value)]
                };
            }

            function getLibraryPolicyStatus(threats) {
                var highest = 0; //ok
                var highestCount = 0;
                for (var i = 0; i < threats.length; ++i) {
                    var level = threats[i].level;
                    var count = threats[i].count;
                    if ((level.toLowerCase() === 'high') && (count > 0) && (highest < 3)) {
                        highest = 3;
                        highestCount = count;
                    } else if ((level.toLowerCase() === 'medium') && (count > 0) && (highest < 2)) {
                        highest = 2;
                        highestCount = count;
                    } else if ((level.toLowerCase() === 'low') && (count > 0) && (highest < 1)) {
                        highest = 1;
                        highestCount = count;
                    }
                }
                return {level: highest, count: highestCount};
            }

            function getMetric(metrics, metricName, title) {
                title = title || metricName;
                return angular.extend((_.find(metrics, { name: metricName }) || { name: title }), { name: title });
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

            function showStatusIcon(item) {
                return item.status && item.status.toLowerCase() !== 'ok';
            }

            ctrl.getDashStatus = function getDashStatus() {

                switch (ctrl.librarySecurityThreatStatus.level) {
                    case 3:
                        return 'alert';

                    case 2:
                        return 'warning';

                    case 1:
                        return 'ignore';

                    default:
                        return 'ok';
                }
            }

            function showDetail(test) {
                $uibModal.open({
                    controller: 'TestDetailsController',
                    controllerAs: 'testDetails',
                    templateUrl: 'components/widgets/codeanalysis/testdetails.html',
                    size: 'lg',
                    resolve: {
                        testResult: function () {
                            return test;
                        }
                    }
                });
            }

            function showLibraryPolicyDetails(type,data) {
                $uibModal.open({
                    controller: 'LibraryPolicyDetailsController',
                    controllerAs: 'libraryPolicyDetails',
                    templateUrl: 'components/widgets/codeanalysis/librarypolicydetails.html',
                    size: 'lg',
                    resolve: {
                        libraryPolicyResult: function () {
                            return ({type: type,data: data});
                        }
                    }
                });
            }
        }
    })
();
