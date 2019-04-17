/**
 * Separate processing code commit data for the product widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('productCommitData', function () {
            return {
                process: process
            }
        });

    function process(dependencies) {
        // unwrap dependencies
        var db = dependencies.db,
            configuredTeam = dependencies.configuredTeam,
            $q = dependencies.$q,
            $timeout = dependencies.$timeout,
            isReload = dependencies.isReload,
            pipelineData = dependencies.pipelineData,
            nowTimestamp = dependencies.nowTimestamp,
            ctrlStages = dependencies.ctrlStages,
            prodStageValue = dependencies.prodStageValue;

        var prodStage = prodStageValue;

        // timestamps
        var now = moment(),
            dateEnds = now.valueOf(),
            ninetyDaysAgo = now.add(-90, 'days').valueOf(),
            dateBegins = ninetyDaysAgo;

        // querying pipeline commits by date will only return production commits that have
        // moved to prod since that last request. this way we can avoid sending 90 days
        // of production commit data with each request. all other environments will show
        // a current snapshot of data
        var collectorItemId = configuredTeam.collectorItemId;


        // get our pipeline commit data. start by seeing if we've already run this request
        db.lastRequest.where('[type+id]').equals(['pipeline-commit', collectorItemId]).first().then(processLastRequestResponse);

        function processLastRequestResponse(lastRequest) {
            // if we already have made a request, just get the delta
            pipelineData
                .commits(dateBegins, nowTimestamp, collectorItemId)
                .then(function (response) {
                    return processPipelineCommitResponse(response, lastRequest);
                })
                .then(processPipelineCommitData)
                .finally(function () {
                    dependencies.cleanseData(db.prodCommit, ninetyDaysAgo);
                });
        }

        function processPipelineCommitResponse(response, lastRequest) {
            if (!response.length) {
                return $q.reject('No response found');
            }

            // we only requested one team so it's safe to assume
            // that it's in the first position
            response = response[0];

            // don't continue saving local data
            if (HygieiaConfig.local && isReload) {
                return response;
            }

            // save the request object so we can get the delta next time as well
            if (lastRequest) {
                lastRequest.timestamp = dateEnds;
                lastRequest.save();
            }
            // need to add a new item
            else {
                db.lastRequest.add({
                    id: collectorItemId,
                    type: 'pipeline-commit',
                    timestamp: dateEnds
                });
            }

            // put all results in the database
            _(response.stages[prodStage]).forEach(function (commit) {
                // extend the commit object with fields we need
                // to search the db
                commit.collectorItemId = collectorItemId;
                commit.timestamp = commit.processedTimestamps[prodStage];

                db.prodCommit.add(commit);
            });

            return response;
        }

        function processPipelineCommitData(team) {
            db.prodCommit.where('[collectorItemId+timestamp]').between([collectorItemId, ninetyDaysAgo], [collectorItemId, dateEnds]).toArray(function (rows) {
                var uniqueRows = _.uniqBy(rows,'scmRevisionNumber');
                team.stages[prodStage] = _(uniqueRows).sortBy('timestamp').reverse().value();

                var teamStageData = {},
                    stageDurations = {},
                    stages = [].concat(ctrlStages); // create a local copy so it doesn't get overwritten

                // go backward through the stages and define commit data.
                // reverse should make it easier to calculate time in the previous stage
                var nextStageName = ''
                _(stages).reverse().forEach(function (currentStageName) {
                    var commits = [], // store our new commit object
                        localStages = [].concat(ctrlStages), // create a copy of the stages
                        previousStages = _(localStages.splice(0, localStages.indexOf(currentStageName))).reverse().value(); // only look for stages before this one

                    // loop through each commit and create our own custom commit object
                    _(team.stages[currentStageName]).forEach(function (commitObj) {
                        var commit = {
                            author: commitObj.scmAuthor || 'NA',
                            message: commitObj.scmCommitLog || 'No message',
                            id: commitObj.scmRevisionNumber,
                            timestamp: commitObj.scmCommitTimestamp,
                            in: {} //placeholder for stage duration data per commit
                        };

                        // make sure this stage exists to track durations
                        if (!stageDurations[currentStageName]) {
                            stageDurations[currentStageName] = [];
                        }

                        // use this commit to calculate time in the current stage
                        var currentStageTimestampCompare = commit.timestamp;
                        if (commitObj.processedTimestamps[currentStageName]) {
                            currentStageTimestampCompare = commitObj.processedTimestamps[currentStageName];
                        }

                        // use this time in our metric calculations
                        var timeInCurrentStage = nowTimestamp - currentStageTimestampCompare;
                        if (nextStageName != '' && team.stages[nextStageName].length == 0) {
                             stageDurations[currentStageName].push(timeInCurrentStage);
                        }

                        // make sure current stage is set
                        commit.in[currentStageName] = timeInCurrentStage;

                        // on each commit, set data for how long it was in each stage by looping
                        // through any previous stage and subtracting its timestamp from the next stage
                        var currentStageTimestamp = commitObj.processedTimestamps[currentStageName];
                        _(previousStages).forEach(function (previousStage) {
                            if (!commitObj.processedTimestamps[previousStage] || isNaN(currentStageTimestamp)) {
                                return;
                            }

                            var previousStageTimestamp = commitObj.processedTimestamps[previousStage],
                                timeInPreviousStage = currentStageTimestamp - previousStageTimestamp;

                            // it is possible that a hot-fix or some other change was made which caused
                            // the commit to skip an earlier environment. In this case just set that
                            // time to 0 so it's considered in the calculation, but does not negatively
                            // take away from the average
                            timeInPreviousStage = Math.max(timeInPreviousStage, 0);

                            // add how long it was in the previous stage
                            commit.in[previousStage] = timeInPreviousStage;

                            // add this number to the stage duration array so it can be used
                            // to calculate each stages average duration individually
                            if (!stageDurations[previousStage]) {
                                stageDurations[previousStage] = [];
                            }

                            // add this time to our duration list
                            stageDurations[previousStage].push(timeInPreviousStage);

                            // now use this as our new current timestamp
                            currentStageTimestamp = previousStageTimestamp;
                        });

                        // add our commit object back
                        commits.push(commit);
                    });

                    // make sure commits are always set
                    teamStageData[currentStageName] = {
                        commits: commits
                    }
                    nextStageName = currentStageName;
                });

                // now that we've added all the duration data for all commits in each stage
                // we can calculate the averages and std deviation and put the data on the stage
                _(stageDurations).forEach(function (durationArray, currentStageName) {
                    if (!teamStageData[currentStageName]) {
                        teamStageData[currentStageName] = {};
                    }

                    var stats = getStageDurationStats(durationArray);
                    angular.extend(teamStageData[currentStageName], {
                        stageAverageTime: stats.mean,
                        stageStdDeviation: stats.deviation
                    })
                });

                // now that we have average and std deviation we can determine if a commit
                // has been in the environment for longer than 2 std deviations in which case
                // it should be marked as a failure
                _(teamStageData).forEach(function (data, stage) {

                    if (!data.stageStdDeviation || !data.commits) {
                        return;
                    }

                    _(data.commits).forEach(function (commit) {
                        // use the time it's been in the existing environment to compare
                        var timeInStage = commit.in[stage];

                        commit.errorState = timeInStage > 2 * data.stageStdDeviation;
                    });
                });

                // create some summary data used in each stage's cell
                _(teamStageData).forEach(function (stageData, stageName) {
                    stageData.summary = {
                        // helper for determining whether this stage has current commits
                        hasCommits: stageData.commits && stageData.commits.length ? true : false,

                        // green block count
                        commitsInsideTimeframe: _(stageData.commits).filter(function (c) {
                            return !c.errorState;
                        }).value().length,

                        // red block count
                        commitsOutsideTimeframe: _(stageData.commits).filter({errorState: true}).value().length,

                        // stage last updated text
                        lastUpdated: (function (stageData) {
                            if (!stageData.commits || !stageData.commits.length) {
                                return false;
                            }

                            // try to get the last commit to enter this stage by evaluating the duration
                            // for this current stage, otherwise use the commit timestamp
                            var lastUpdatedDuration = _(stageData.commits).map(function (commit) {
                                    return commit.in[stageName] || moment().valueOf() - commit.timestamp;
                                }).min(),
                                lastUpdated = moment().add(-1 * lastUpdatedDuration, 'milliseconds');

                            return {
                                longDisplay: lastUpdated.format('MMMM Do YYYY, h:mm:ss a'),
                                shortDisplay: lastUpdated.dash('ago')
                            }
                        })(stageData),

                        // stage deviation
                        deviation: (function (stageData) {
                            if (stageData.stageStdDeviation == undefined) {
                                return false;
                            }

                            // determine how to display the standard deviation
                            var number = moment.duration(2 * stageData.stageStdDeviation).minutes(),
                                desc = 'min';

                            if (number > 60 * 24) {
                                desc = 'day';
                                number = Math.round(number / 24 / 60);
                            }
                            else if (number > 60) {
                                desc = 'hour';
                                number = Math.round(number / 60);
                            }

                            return {
                                number: number,
                                descriptor: desc
                            }
                        })(stageData),

                        average: (function (stageData) {
                            // determine how to display the average time
                            if (!stageData.stageAverageTime) {
                                return false;
                            }

                            var average = moment.duration(stageData.stageAverageTime);

                            return {
                                days: Math.floor(average.asDays()),
                                hours: average.hours(),
                                minutes: average.minutes()
                            }
                        })(stageData)
                    };
                });

                // calculate info used in prod cell
                var commitTimeToProd;
                var commitArray = _(team.stages)
                // limit to prod
                    .filter(function (val, key) {
                        return key == prodStage
                    })
                    // make all commits a single array
                    .reduce(function (num, commits) {
                        return num + commits;
                    });
                if(!angular.isUndefined(commitArray)){
                    // calculate their time to prod
                    commitTimeToProd = commitArray.map(function (commit) {
                            return {
                                duration: commit.processedTimestamps[prodStage] - commit.scmCommitTimestamp,
                                commitTimestamp: commit.scmCommitTimestamp
                            };
                        });
                }

                var teamProdData = {
                    averageDays: '--',
                    totalCommits: 0
                },commitTimeToProd;

                teamProdData.totalCommits = !angular.isUndefined(commitTimeToProd)?commitTimeToProd.length:0;

                if (!angular.isUndefined(commitTimeToProd)?commitTimeToProd.length:0 > 1) {
                    var averageDuration = _(commitTimeToProd).map('duration').reduce(function (a, b) {
                            return a + b;
                        }) / commitTimeToProd.length;

                    teamProdData.averageDays = Math.floor(moment.duration(averageDuration).asDays());

                    var plotData = _(commitTimeToProd).map(function (ttp) {
                        var daysAgo = -1 * moment.duration(moment().diff(ttp.commitTimestamp)).asDays();
                        return [daysAgo, ttp.duration];
                    }).value();

                    var averageToProdResult = regression('linear', plotData);
                    teamProdData.trendUp = averageToProdResult.equation[0] > 0;
                }

                // handle the api telling us which stages need configuration
                if (team.unmappedStages) {
                    for (var stageName in teamStageData) {
                        teamStageData[stageName].needsConfiguration = team.unmappedStages.indexOf(stageName) != -1;
                    }
                }

                $timeout(function() {
                    dependencies.setTeamData(team.collectorItemId, {
                    stages: teamStageData,
                    prod: teamProdData,
                    prodStage:prodStage
                    });
                });
            });
        }

        function getStageDurationStats(a) {
            var r = {mean: 0, variance: 0, deviation: 0}, t = a.length;
            for (var m, s = 0, l = t; l--; s += a[l]);
            for (m = r.mean = s / t, l = t, s = 0; l--; s += Math.pow(a[l] - m, 2));
            return r.deviation = Math.sqrt(r.variance = s / t), r;
        }
    }
})();
