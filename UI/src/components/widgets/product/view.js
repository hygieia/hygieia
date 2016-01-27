(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productViewController', productViewController);

    productViewController.$inject = ['$scope', '$modal', 'pipelineData', '$location', 'collectorData'];
    function productViewController($scope, $modal, pipelineData, $location, collectorData) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties
        ctrl.stages = ['Commit', 'Build', 'DEV', 'QA', 'INT', 'PERF', 'PROD'];

        // public methods
        ctrl.load = load;
        ctrl.editTeam = editTeam;
        ctrl.addTeam = addTeam;
        ctrl.openDashboard = openDashboard;
        ctrl.viewTeamEnvDetails = viewTeamEnvDetails;

        ctrl.getTeamStageData = function(team, stage) {
            if (!hasStageData(team, stage)) {
                return false;
            }

            var stageData = team.stages[stage],
                lastUpdated = moment(_(stageData.commits).max('timestamp').value().timestamp),
                deviationNum = moment(stageData.stageStdDeviation).minutes(),
                deviationDesc = 'min';

            if(deviationNum > 60*24) {
                deviationDesc = 'day';
                deviationNum = Math.round(deviationNum / 24 / 60);
            }
            else if (deviationNum > 60) {
                deviationDesc = 'hour';
                deviationNum = Math.round(deviationNum / 60);
            }

            var average = moment(stageData.stageAverageTime);

            return {
                commitsInsideTimeframe: stageData.commitsInsideTimeframe,
                commitsOutsideTimeframe: stageData.commitsOutsideTimeframe,
                lastUpdated: {
                    longDisplay: lastUpdated.format('MMMM Do YYYY, h:mm:ss a'),
                    shortDisplay: lastUpdated.dash('ago')
                },
                deviation: {
                    number: deviationNum,
                    descriptor: deviationDesc
                },
                average: {
                    days: average.days(),
                    hours: average.hours(),
                    minutes: average.minutes()
                }
            }
        };

        function hasStageData(team, stage) {
            return team.stages && team.stages[stage];
        }

        function openDashboard(item) {
            collectorData.itemsByType('product').then(function(response) {
                _(response).forEach(function(board) {
                    if (item.collectorItemId == board.id) {
                        $location.path('/dashboard/' + board.options.dashboardId);
                    }
                });
            });
        }

        function load() {
            var options = $scope.widgetConfig.options;
            console.log($scope.dashboard);
            console.log(options);

            if (options && options.teams) {
                ctrl.configuredTeams = options.teams;
            }

            getCommitData(options.teams);
        }

        function editTeam(team) {

            $modal.open({
                templateUrl: 'components/widgets/product/edit-team/edit-team.html',
                controller: 'editTeamController',
                controllerAs: 'ctrl',
                resolve: {
                    editTeamConfig: function() {
                        return {
                            team: team
                        }
                    }
                }
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                var options = $scope.widgetConfig.options;

                // take the collector item out of the team array
                if(config.remove) {
                    // do remove
                    var keepTeams = [];

                    _(options.teams).forEach(function(team) {
                        if(team.collectorItemId != config.collectorItemId) {
                            keepTeams.push(team);
                        }
                    });

                    options.teams = keepTeams;
                }
                else {
                    for(var x=0;x<options.teams.length;x++) {
                        if(options.teams[x].collectorItemId == config.collectorItemId) {
                            options.teams[x] = config;
                        }
                    }
                }

                updateWidgetOptions(options);
            });
        }

        function addTeam() {

            $modal.open({
                templateUrl: 'components/widgets/product/add-team/add-team.html',
                controller: 'addTeamController',
                controllerAs: 'ctrl'
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                // prepare our response for the widget upsert
                var options = $scope.widgetConfig.options;

                // make sure it's an array
                if(!options.teams || !options.teams.length) {
                    options.teams = [];
                }

                // add our new config to the array
                options.teams.push(config);

                updateWidgetOptions(options);
            });
        }

        function updateWidgetOptions(options) {
            // get a list of collector ids
            var collectorItemIds = [];
            _(options.teams).forEach(function(team) {
                collectorItemIds.push(team.collectorItemId);
            });

            var data = {
                name: 'product',
                componentId: $scope.dashboard.application.components[0].id,
                collectorItemIds: collectorItemIds,
                options: options
            };

            console.log('Upsert widget', data);
            $scope.upsertWidget(data);
        }

        function viewTeamEnvDetails() {
            $modal.open({
                templateUrl: 'components/widgets/product/environment-commits/environment-commits.html',
                controller: 'productEnvironmentCommitController',
                controllerAs: 'ctrl',
                size: 'lg'
            });
        }

        function getStageDurationStats(a) {
            var r = {mean: 0, variance: 0, deviation: 0}, t = a.length;
            for(var m, s = 0, l = t; l--; s += a[l]);
            for(m = r.mean = s / t, l = t, s = 0; l--; s += Math.pow(a[l] - m, 2));
            return r.deviation = Math.sqrt(r.variance = s / t), r;
        }

        function teamHasStageCommits(team, stage) {
            return team.stages[stage] && team.stages[stage].commits && team.stages[stage].commits.length;
        }

        function getCommitData(teams) {
            var now = moment(),
                end = now.format('x'),
                start = now.subtract(90, 'days').format('x');

            pipelineData.commits(start, end, _(teams).pluck('collectorItemId').value()).then(function(teams) {
                // prep some test data
                /*
                var buildCommits = [];
                var QACommits = [];
                function getRandomInt(min, max) {
                    return Math.floor(Math.random() * (max - min + 1)) + min;
                }

                for(var x=0;x<teams[0].stages.Commit.commits.length;x++) {
                    if (x % 2 == 0) {
                        var el = teams[0].stages.Commit.commits.splice(x, 1)[0];
                        el.processedTimestamps.Build = el.processedTimestamps.Commit + (1000*60*getRandomInt(15, 2500));
                        buildCommits.push(el);
                    }

                    if(x % 4 == 0) {
                        var el = buildCommits.splice(buildCommits.length - 1, 1)[0];
                        el.processedTimestamps.QA = el.processedTimestamps.Build + (1000*60*getRandomInt(15, 2500));
                        QACommits.push(el);
                    }
                }

                 teams[0].stages.Build = {
                    commits: buildCommits
                };
                 teams[0].stages.QA = {
                    commits: QACommits
                };

                console.log(JSON.stringify(teams));
                */

                // process response
                var result = {};

                // loop through each team
                _(teams).each(function(team) {
                    var teamStageData = {},
                        stageDurations = {},
                        stages = [].concat(ctrl.stages); // create a local copy so lodash doesn't overwrite it

                    // go backward through the stages and define commit data.
                    // reverse should make it easier to calculate time in the previous stage
                    _(stages).reverse().forEach(function(currentStageName) {

                        // make sure there are commits in that stage, otherwise skip it
                        if (!teamHasStageCommits(team, currentStageName)) {
                            return;
                        }

                        var stage = team.stages[currentStageName],
                            commits = [],
                            localStages = [].concat(ctrl.stages),
                            previousStages = _(localStages.splice(0, localStages.indexOf(currentStageName))).reverse().value();

                        // loop through each commit and create our own custom commit object
                        _(stage.commits).forEach(function(commitObj) {
                            var commit = {
                                author: commitObj.commit.scmAuthor || 'NA',
                                message: commitObj.commit.scmCommitLog || 'No message',
                                timestamp: commitObj.commit.scmCommitTimestamp
                            };

                            // on each commit, set data for how long it was in each stage by looping
                            // through any previous stage and subtracting its timestamp from the next stage
                            var currentStageTimestamp = commitObj.processedTimestamps[currentStageName];
                            _(previousStages).forEach(function(previousStage) {
                                if(!commitObj.processedTimestamps[previousStage]) {
                                    return;
                                }

                                var previousStageTimestamp = commitObj.processedTimestamps[previousStage],
                                    timeInPreviousStage = currentStageTimestamp - previousStageTimestamp;

                                commit['in' + previousStage] = timeInPreviousStage;
                                currentStageTimestamp = previousStageTimestamp;

                                // add this number to the stage duration array so it can be used
                                // to calculate each stages average duration individually
                                if(!stageDurations[previousStage]) {
                                    stageDurations[previousStage] = [];
                                }

                                stageDurations[previousStage].push(timeInPreviousStage);
                            });

                            // add our commit object back
                            commits.push(commit);
                        });

                        teamStageData[currentStageName] = {
                            commits: commits
                        }

                    });

                    // now that we've added all the duration data for all commits in each stage
                    // we can calculate the averages and std deviation
                    _(stageDurations).forEach(function(durationArray, currentStageName) {
                        var stats = getStageDurationStats(durationArray)
                        console.log(currentStageName, stats);
                        angular.extend(teamStageData[currentStageName], {
                            stageAverageTime: stats.mean,
                            stageStdDeviation: stats.deviation
                        })
                    });

                    // now that we have average and std deviation we can determine if a commit
                    // has been in the environment for longer than 2 std deviations in which case
                    // it should be marked as a failure
                    _(teamStageData).forEach(function(data, stage) {

                        if(!data.stageStdDeviation || !data.commits) {
                            return;
                        }

                        var insideTimeframe = 0,
                            outsideTimeframe = 0;

                        _(data.commits).forEach(function(commit) {
                            var now = moment().format('x'),
                                timeInStage = now - commit.timestamp;

                            commit.showRed = timeInStage > 2 * commit.stageStdDeviation;

                            if(commit.showRed) {
                                outsideTimeframe++;
                            } else {
                                insideTimeframe++;
                            }
                        });

                        data.commitsInsideTimeframe = insideTimeframe;
                        data.commitsOutsideTimeframe = outsideTimeframe;
                    });

                    // set all the team data in a key that w/*e can
                    // easily get to with collector item id
                    //result[team.collectorItemId] = teamStageData;
                    _(ctrl.configuredTeams).filter({'collectorItemId': team.collectorItemId}).forEach(function(configuredTeam) {
                        angular.extend(configuredTeam, {
                            stages : teamStageData
                        });
                    });
                    angular.extend(team)
                });


                console.log(ctrl.configuredTeams);
            });
        }
    }
})();
