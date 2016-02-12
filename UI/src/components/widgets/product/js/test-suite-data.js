/**
 * Separate processing code test suite data for the product widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('productTestSuiteData', function () {
            return {
                process: process
            }
        });

    function process(dependencies) {
        var db = dependencies.db,
            componentId = dependencies.componentId,
            collectorItemId = dependencies.collectorItemId,
            $timeout = dependencies.$timeout,
            $q = dependencies.$q,
            isReload = dependencies.isReload,
            testSuiteData = dependencies.testSuiteData;

        db.lastRequest.where('[type+id]').equals(['test-suite', componentId]).first().then(function (lastRequest) {
            var now = moment(),
                dateEnds = now.valueOf(),
                ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                dateBegins = ninetyDaysAgo;

            // if we already have made a request, just get the delta
            if (lastRequest) {
                dateBegins = lastRequest.timestamp;
            }

            testSuiteData
                .details({componentId: componentId, endDateBegins: dateBegins, endDateEnds: dateEnds, depth: 1})
                .then(function (response) {
                    // since we're only requesting a minute we'll probably have nothing
                    if (!response || !response.result || !response.result.length) {
                        return isReload ? $q.reject('No new data') : false;
                    }

                    // save the request object so we can get the delta next time as well
                    if (lastRequest) {
                        lastRequest.timestamp = dateEnds;
                        lastRequest.save();
                    }
                    // need to add a new item
                    else {
                        db.lastRequest.add({
                            id: componentId,
                            type: 'test-suite',
                            timestamp: dateEnds
                        });
                    }

                    // put all results in the database
                    _(response.result).forEach(function (result) {
                        var totalPassed = 0,
                            totalTests = 0;

                        _(result.testCapabilities).forEach(function (capabilityResult) {
                            totalPassed += capabilityResult.successTestSuiteCount;
                            totalTests += capabilityResult.totalTestSuiteCount;
                        });

                        var test = {
                            componentId: componentId,
                            collectorItemId: result.collectorItemId,
                            name: result.description,
                            timestamp: result.endTime,
                            successCount: totalPassed,//result.successCount,
                            totalCount: totalTests//result.totalCount
                        };

                        db.testSuite.add(test);
                    });
                })
                .then(function () {
                    // now that all the delta data has been saved, request
                    // and process 90 days worth of it
                    db.testSuite.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function (rows) {
                        if (!rows.length) {
                            return;
                        }

                        // make sure it's sorted with the most recent first (largest timestamp)
                        rows = _(rows).sortBy('timestamp').reverse().value();

                        // prepare the data for the regression test mapping days ago on the x axis
                        var now = moment(),
                            data = _(rows).map(function (result) {
                                var daysAgo = -1 * moment.duration(now.diff(result.timestamp)).asDays(),
                                    totalPassed = result.successCount || 0,
                                    totalTests = result.totalCount,
                                    percentPassed = totalTests ? totalPassed / totalTests : 0;

                                return [daysAgo, percentPassed];
                            }).value();

                        var passedPercentResult = regression('linear', data),
                            passedPercentTrendUp = passedPercentResult.equation[0] > 0;

                        // get the most recent record for current metric for each collectorItem id
                        var lastRunResults = _(rows).groupBy('collectorItemId').map(function (items, collectorItemId) {
                            var lastRun = _(items).sortBy('timestamp').reverse().first();

                            return {
                                success: lastRun.successCount || 0,
                                total: lastRun.totalCount || 0
                            }
                        });

                        var totalSuccess = lastRunResults.pluck('success').reduce(function (a, b) {
                                return a + b
                            }),
                            totalResults = lastRunResults.pluck('total').reduce(function (a, b) {
                                return a + b
                            });

                        // use $timeout so that it will apply on the next digest
                        $timeout(function () {
                            // update data for the UI
                            dependencies.setTeamData(collectorItemId, {
                                data: {
                                    testSuite: rows
                                },
                                summary: {
                                    functionalTestsPassed: {
                                        number: totalResults ? Math.round(totalSuccess / totalResults * 100) : 0,
                                        trendUp: passedPercentTrendUp,
                                        successState: passedPercentTrendUp
                                    }
                                }
                            });
                        });
                    });
                })
                .finally(function () {
                    dependencies.cleanseData(db.testSuite, ninetyDaysAgo);
                });
        });
    }
})();