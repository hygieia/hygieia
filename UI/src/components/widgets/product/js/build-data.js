/**
 * Separate processing build data for the product widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('productBuildData', function() {
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
            buildData = dependencies.buildData;

        // timestamps
        var now = moment(),
            dateEnds = now.valueOf(),
            ninetyDaysAgo = now.add(-90, 'days').valueOf(),
            dateBegins = ninetyDaysAgo;

        db.lastRequest.where('[type+id]').equals(['build-data', componentId]).first().then(processLastRequestResponse);

        function processLastRequestResponse(lastRequest) {
            // if we already have made a request, just get the delta
            if(lastRequest) {
                dateBegins = lastRequest.timestamp;
            }

            buildData
                .details({componentId: componentId, endDateBegins: dateBegins, endDateEnds: dateEnds})
                .then(function(response) {
                    processBuildDetailsResponse(response, lastRequest);
                })
                .then(processBuildDetailsData)
                .finally(function() {
                    dependencies.cleanseData(db.buildData, ninetyDaysAgo);
                });
        }

        function processBuildDetailsResponse(response, lastRequest) {
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
                    type: 'build-data',
                    timestamp: dateEnds
                });
            }

            // put all results in the database
            _(response.result).forEach(function(result) {
                var build = {
                    componentId: componentId,
                    timestamp: result.endTime,
                    number: result.number,
                    success: result.buildStatus.toLowerCase() == 'success',
                    inProgress: result.buildStatus.toLowerCase() == 'inprogress'
                };

                db.buildData.add(build);
            });
        }

        function processBuildDetailsData() {
            return db.buildData.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function(rows) {
                if(!rows.length) {
                    return;
                }

                // make sure it's sorted with the most recent first (largest timestamp)
                rows = _(rows).sortBy('timestamp').reverse().value();

                var latestBuild = rows[0];

                rows = _(rows).filter({inProgress:false}).value();

                var now = moment(),
                    successRateData = _(rows).groupBy(function(build) {
                        return -1 * Math.floor(moment.duration(now.diff(moment(build.timestamp).startOf('day').valueOf())).asDays());
                    }).map(function(builds, key) {
                        key = parseFloat(key); // make sure it's a number

                        var successfulBuilds = _(builds).filter({success:true}).value().length,
                            totalBuilds = builds.length;

                        return [key, totalBuilds > 0 ? successfulBuilds / totalBuilds : 0];
                    }).value(),
                    fixedBuildData = [],
                    fixedBuildDetails = [];

                var lastFailedBuild = false;
                _(rows).reverse().forEach(function(build) {
                    // we have a failed build. need a
                    // successful one to compare it to
                    if(lastFailedBuild) {
                        if(build.success) {
                            var daysAgo = -1 * moment.duration(now.diff(lastFailedBuild.timestamp)).asDays(),
                                timeToFixInMinutes = moment.duration(moment(build.timestamp).diff(lastFailedBuild.timestamp)).asMinutes()

                            // add this to our regression data
                            fixedBuildData.push([daysAgo, timeToFixInMinutes]);

                            // create a custom object to pass to quality details
                            fixedBuildDetails.push({
                                brokenBuild: lastFailedBuild,
                                fixedBuild: build
                            });

                            // reset the failed build so we can find the next one
                            lastFailedBuild = false;
                        }

                        return;
                    }

                    // we need a failed build
                    if(!build.success) {
                        lastFailedBuild = build;
                    }
                });

                var successRateResponse = regression('linear', successRateData),
                    successRateTrendUp = successRateResponse.equation[0] > 0,
                    totalSuccessfulBuilds = _(rows).filter({success:true}).value().length,
                    totalBuilds = rows.length,
                    successRateAverage = totalBuilds ? totalSuccessfulBuilds / totalBuilds : 0;

                var buildData = {
                    data: {
                        buildSuccess: successRateData,
                        fixedBuild: fixedBuildDetails
                    },
                    summary: {
                        buildSuccess: {
                            number: Math.round(successRateAverage * 100),
                            trendUp: successRateTrendUp,
                            successState: successRateTrendUp
                        }
                    },
                    latestBuild: {
                        number: latestBuild.number,
                        success: latestBuild.success,
                        inProgress: latestBuild.inProgress
                    }
                };

                // only calculate fixed build data if it exists
                if(fixedBuildData.length) {
                    var buildFixRateResponse = regression('linear', fixedBuildData),
                        buildFixRateTrendUp = buildFixRateResponse.equation[0] > 0,
                        buildFixRateAverage = fixedBuildData.length ? Math.round(_(fixedBuildData).map(function(i) { return i[1]; }).reduce(function(a,b){ return a+b; }) / fixedBuildData.length) : false,
                        buildFixRateMetric = 'm',
                        minPerDay = 24*60;

                    if (buildFixRateAverage > minPerDay) {
                        buildFixRateAverage = Math.round(buildFixRateAverage / minPerDay);
                        buildFixRateMetric = 'd';
                    }
                    else if(buildFixRateAverage > 60) {
                        buildFixRateAverage = Math.round(buildFixRateAverage / 60);
                        buildFixRateMetric = 'h'
                    }

                    buildData.summary['buildFix'] = {
                        number: buildFixRateAverage,
                        trendUp: buildFixRateTrendUp,
                        successState: !buildFixRateTrendUp,
                        metric: buildFixRateMetric
                    };
                }

                // use $timeout so that it will apply on the next digest
                $timeout(function() {
                    // update data for the UI
                    dependencies.setTeamData(collectorItemId, buildData);
                });
            });
        }
    }
})();