/**
 * Separate processing code security analysis data for the product widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('productSecurityAnalysisData', function () {
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

        // get our security analysis data. start by seeing if we've already run this request
        db.lastRequest.where('[type+id]').equals(['security-analysis', componentId]).first()
            .then(function (lastRequest) {
                // if we already have made a request, just get the delta
                if (lastRequest) {
                    dateBegins = lastRequest.timestamp;
                }

                codeAnalysisData
                    .securityDetails({componentId: componentId, dateBegins: dateBegins, dateEnds: dateEnds})
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
                                type: 'security-analysis',
                                timestamp: dateEnds
                            });
                        }

                        // put all results in the database
                        _(response.result).forEach(function (result) {
                            var metrics = result.metrics,
                                analysis = {
                                    componentId: componentId,
                                    timestamp: result.timestamp,
                                    blocker: parseInt(getCaMetric(metrics, 'blocker')),
                                    critical: parseInt(getCaMetric(metrics, 'critical')),
                                    major: parseInt(getCaMetric(metrics, 'major'))
                                };

                            db.securityAnalysis.add(analysis);
                        });
                    })
                    .then(function () {
                        db.securityAnalysis.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function (rows) {
                            if (!rows.length) {
                                return;
                            }

                            // make sure it's sorted with the most recent first (largest timestamp)
                            rows = _(rows).sortBy('timestamp').reverse().value();

                            // prepare the data for the regression test mapping days ago on the x axis
                            var now = moment(),
                                securityIssues = _(rows).map(function (row) {
                                    var daysAgo = -1 * moment.duration(now.diff(row.timestamp)).asDays();
                                    return [daysAgo, row.major + row.critical + row.blocker];
                                }).value();

                            var securityIssuesResult = regression('linear', securityIssues),
                                securityIssuesTrendUp = securityIssuesResult.equation[0] > 0;


                            // get the most recent record for current metric
                            var latestResult = rows[0];

                            // use $timeout so that it will apply on the next digest
                            $timeout(function () {
                                // update data for the UI
                                dependencies.setTeamData(collectorItemId, {
                                    data: {
                                        securityAnalysis: rows
                                    },
                                    summary: {
                                        securityIssues: {
                                            number: latestResult.major + latestResult.critical + latestResult.blocker,
                                            trendUp: securityIssuesTrendUp,
                                            successState: !securityIssuesTrendUp
                                        }
                                    }
                                });
                            });
                        });
                    })
                    .finally(function () {
                        dependencies.cleanseData(db.securityAnalysis, ninetyDaysAgo);
                    });
            });
    }
})();