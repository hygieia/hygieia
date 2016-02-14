/**
 * Separate processing code analysis data for the product widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('productCodeAnalysisData', function() {
            return {
                process: process
            }
        });

    function process(dependencies) {
        // unwrap dependencies
        var db = dependencies.db,
            componentId = dependencies.componentId,
            collectorItemId = dependencies.collectorItemId,
            $timeout = dependencies.$timeout,
            $q = dependencies.$q,
            isReload = dependencies.isReload,
            getCaMetric = dependencies.getCaMetric,
            codeAnalysisData = dependencies.codeAnalysisData;

        // timestamps
        var now = moment(),
            dateEnds = now.valueOf(),
            ninetyDaysAgo = now.add(-90, 'days').valueOf(),
            dateBegins = ninetyDaysAgo;

        db.lastRequest.where('[type+id]').equals(['code-analysis', componentId]).first().then(processLastRequestResponse);

        function processLastRequestResponse(lastRequest) {
            // if we already have made a request, just get the delta
            if(lastRequest) {
                dateBegins = lastRequest.timestamp;
            }

            // request our data
            codeAnalysisData
                .staticDetails({componentId: componentId, dateBegins: dateBegins, dateEnds: dateEnds})
                .then(function(response) {
                    processStaticAnalysisResponse(response, lastRequest, dateEnds);
                })
                .then(processStaticAnalysisData)
                .finally(function() {
                    dependencies.cleanseData(db.codeAnalysis, ninetyDaysAgo);
                });
        }

        function processStaticAnalysisResponse(response, lastRequest, dateEnds) {
            // since we're only requesting a minute we'll probably have nothing
            if(!response || !response.result || !response.result.length) {
                return isReload ? $q.reject('No new data') : false;
            }

            // save the request object so we can get the delta next time as well
            if(lastRequest) {
                lastRequest.timestamp = dateEnds;
                lastRequest.save();
            }
            // need to add a new item
            else {
                db.lastRequest.add({
                    id: componentId,
                    type: 'code-analysis',
                    timestamp: dateEnds
                });
            }

            // put all results in the database
            _(response.result).forEach(function(result) {
                var metrics = result.metrics,
                    analysis = {
                        componentId: componentId,
                        timestamp: result.timestamp,
                        coverage: getCaMetric(metrics, 'coverage'),
                        lineCoverage: getCaMetric(metrics, 'line_coverage'),
                        violations: getCaMetric(metrics, 'violations'),
                        criticalViolations: getCaMetric(metrics, 'critical_violations'),
                        majorViolations: getCaMetric(metrics, 'major_violations'),
                        blockerViolations: getCaMetric(metrics, 'blocker_violations'),
                        testSuccessDensity: getCaMetric(metrics, 'test_success_density'),
                        testErrors: getCaMetric(metrics, 'test_errors'),
                        testFailures: getCaMetric(metrics, 'test_failures'),
                        tests: getCaMetric(metrics, 'tests')
                    };

                db.codeAnalysis.add(analysis);
            });
        }

        function processStaticAnalysisData() {
            // now that all the delta data has been saved, request
            // and process 90 days worth of it
            db.codeAnalysis.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function(rows) {
                if(!rows.length) {
                    return;
                }

                // make sure it's sorted with the most recent first (largest timestamp)
                rows = _(rows).sortBy('timestamp').reverse().value();

                // prepare the data for the regression test mapping days ago on the x axis
                var now = moment(),
                    codeIssues = _(rows).map(function(row) {
                        var daysAgo = -1 * moment.duration(now.diff(row.timestamp)).asDays(),
                            totalViolations = row.violations + row.criticalViolations + row.majorViolations + row.blockerViolations;
                        return [daysAgo, totalViolations];
                    }).value(),
                    codeCoverage = _(rows).map(function(row) {
                        var daysAgo = -1 * moment.duration(now.diff(row.timestamp)).asDays();
                        return [daysAgo, row.lineCoverage]
                    }).value(),
                    unitTestSuccess = _(rows).map(function(row) {
                        var daysAgo = -1 * moment.duration(now.diff(row.timestamp)).asDays();
                        return [daysAgo, row.testSuccessDensity]
                    }).value();

                var codeIssuesResult = regression('linear', codeIssues),
                    codeIssuesTrendUp = codeIssuesResult.equation[0] > 0;

                var codeCoverageResult = regression('linear', codeCoverage),
                    codeCoverageTrendUp = codeCoverageResult.equation[0] > 0;

                var unitTestSuccessResult = regression('linear', unitTestSuccess),
                    unitTestSuccessTrendUp = unitTestSuccessResult.equation[0] > 0;


                // get the most recent record for current metric
                var latestResult = rows[0];

                // use $timeout so that it will apply on the next digest
                $timeout(function() {
                    // update data for the UI
                    dependencies.setTeamData(collectorItemId, {
                        data: {
                            codeAnalysis: rows
                        },
                        summary: {
                            codeIssues: {
                                number: latestResult.violations,
                                trendUp: codeIssuesTrendUp,
                                successState: !codeIssuesTrendUp
                            },
                            codeCoverage: {
                                number: Math.round(latestResult.lineCoverage),
                                trendUp: codeCoverageTrendUp,
                                successState: codeCoverageTrendUp
                            },
                            unitTests: {
                                number: Math.round(latestResult.testSuccessDensity),
                                trendUp: unitTestSuccessTrendUp,
                                successState: unitTestSuccessTrendUp
                            }
                        }
                    });
                });
            });
        }
    }
})();