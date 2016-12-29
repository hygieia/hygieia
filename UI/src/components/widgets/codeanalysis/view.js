(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CodeAnalysisViewController', CodeAnalysisViewController);

    CodeAnalysisViewController.$inject = ['$scope', 'codeAnalysisData', 'testSuiteData', '$q', '$filter', '$modal'];
    function CodeAnalysisViewController($scope, codeAnalysisData, testSuiteData, $q, $filter, $modal) {
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

        coveragePieChart({});

        ctrl.load = function() {
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
            return $q.all([
                codeAnalysisData.staticDetails(caRequest).then(processCaResponse),
                codeAnalysisData.securityDetails(saRequest).then(processSaResponse),
                testSuiteData.details(testRequest).then(processTestResponse)
            ]);
        };

        function processCaResponse(response) {
            var deferred = $q.defer();
            var caData = _.isEmpty(response.result) ? {} : response.result[0];

            ctrl.versionNumber = caData.version;

            ctrl.rulesCompliance = getMetric(caData.metrics, 'violations_density');

            ctrl.technicalDebt = getMetric(caData.metrics, 'sqale_index');

		//the JSON contains the required info in ctrl.technicalDebt.formattedValue, no need to calculate	 
            //ctrl.technicalDebt.formattedValue = calculateTechnicalDebt(ctrl.technicalDebt.value);

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

            //ctrl.versionNumber = saData.version;

            ctrl.securityIssues = [
                getMetric(saData.metrics, 'blocker', 'Blocker'),
                getMetric(saData.metrics, 'critical', 'Critical'),
                getMetric(saData.metrics, 'major', 'Major'),
                getMetric(saData.metrics, 'minor', 'Minor')
            ];

            deferred.resolve(response.lastUpdated);
            return deferred.promise;
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
                    //var ind;
                    //for (ind = 0; ind < testCap.testSuites.length; ++ind) {
                    //    var testSuite = capability.testSuites[ind];
                    //    result.failureCount += testSuite.failedTestCaseCount;
                    //    result.successCount += testSuite.successTestCaseCount;
                    //    result.skippedCount += testSuite.skippedTestCaseCount;
                    //    result.totalCount += testSuite.totalTestCaseCount;
                    //}
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
                series: [ lineCoverage.value, (100 - lineCoverage.value) ]
            };
        }


        function getMetric(metrics, metricName, title) {
            title = title || metricName;
            return angular.extend((_.findWhere(metrics, { name: metricName }) || { name: title }), { name: title });
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
            return Math.ceil(value/factor) + suffix;
        }

        function showStatusIcon(item) {
            return item.status && item.status.toLowerCase() != 'ok';
        }


        function showDetail(test) {
            $modal.open({
                controller: 'TestDetailsController',
                controllerAs: 'testDetails',
                templateUrl: 'components/widgets/codeanalysis/testdetails.html',
                size: 'lg',
                resolve: {
                    testResult: function() {
                        return test;
                    }
                }
            });
        }
    }
})();
