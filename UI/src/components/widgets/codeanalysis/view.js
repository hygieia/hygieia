(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('CodeAnalysisViewController', CodeAnalysisViewController);

    CodeAnalysisViewController.$inject = ['$scope', 'codeAnalysisData', 'testSuiteData', '$q', '$filter'];
    function CodeAnalysisViewController($scope, codeAnalysisData, testSuiteData, $q, $filter) {
        var ctrl = this;

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };

        ctrl.showStatusIcon = showStatusIcon;

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
            ctrl.technicalDebt.formattedValue = calculateTechnicalDebt(ctrl.technicalDebt.value);

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
            var testResult = _.isEmpty(response.result) ? { testSuites: []} : response.result[0];
            var allZeros = {
                failureCount: 0, errorCount: 0, skippedCount: 0, totalCount: 0
            };

            // Aggregate the counts of all Functional test suites
            var aggregate = _.reduce(_.filter(testResult.testSuites, { type: "Functional" }), function(result, suite) {
                result.failureCount += suite.failureCount;
                result.errorCount += suite.errorCount;
                result.skippedCount += suite.skippedCount;
                result.totalCount += suite.totalCount;
                return result;
            }, allZeros);
            var passed = aggregate.totalCount - aggregate.failureCount - aggregate.errorCount - aggregate.skippedCount;
            var allPassed = aggregate.errorCount === 0 && aggregate.failureCount === 0;
            var success = allPassed ? 100 : ((passed / (aggregate.totalCount)) * 100);

            ctrl.functionalTests = [];

            ctrl.functionalTests.push({
                name: 'Success',
                formattedValue: aggregate.totalCount === 0 ? '-' : $filter('number')(success, 1) + '%',
                status: allPassed ? 'Ok' : 'Alert',
                statusMessage: allPassed ? '' : 'Success percent < 100'
            });

            ctrl.functionalTests.push({
                name: 'Failures',
                formattedValue: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.failureCount, 0),
                status: aggregate.failureCount === 0 ? 'Ok' : 'Alert',
                statusMessage: aggregate.failureCount === 0 ? '' : 'Failure count > 0'
            });

            ctrl.functionalTests.push({
                name: 'Errors',
                formattedValue: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.errorCount, 0),
                status: aggregate.errorCount === 0 ? 'Ok' : 'Alert',
                statusMessage: aggregate.errorCount === 0 ? '' : 'Error count > 0'
            });

            ctrl.functionalTests.push({
                name: 'Tests',
                formattedValue: aggregate.totalCount === 0 ? '-' : $filter('number')(aggregate.totalCount, 0),
                status: 'Ok',
                statusMessage: ''
            });

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
    }
})();
