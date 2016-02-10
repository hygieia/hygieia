(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productViewController', productViewController);

    productViewController.$inject = ['$scope', '$document', '$modal', '$location', '$q', '$routeParams', '$timeout', 'buildData', 'codeAnalysisData', 'collectorData', 'dashboardData', 'pipelineData', 'testSuiteData'];
    function productViewController($scope, $document, $modal, $location, $q, $routeParams, $timeout, buildData, codeAnalysisData, collectorData, dashboardData, pipelineData, testSuiteData) {
        /*jshint validthis:true */
        var ctrl = this;

        // private properties
        var teamDashboardDetails = {},
            isReload = null;

        // setup our local db
        var db = new Dexie('ProductPipelineDb');
        Dexie.Promise.on('error', function(err) {
            // Log to console or show en error indicator somewhere in your GUI...
            console.log('Uncaught Dexie error: ' + err);
        });

        // define our schemas
        db.version(1).stores({
            lastRequest: '[type+id]',
            testSuite: '++id,timestamp,[componentId+timestamp]',
            codeAnalysis: '++id,timestamp,[componentId+timestamp]',
            securityAnalysis: '++id,timestamp,[componentId+timestamp]',
            buildData: '++id,timestamp,[componentId+timestamp]',
            prodCommit: '++id,timestamp,[collectorItemId+timestamp]'
        });

        // create classes
        var LastRequest = db.lastRequest.defineClass({
            id: String,
            type: String,
            timestamp: Number
        });

        // ad a convenience method to save back the request
        LastRequest.prototype.save = function() {
            db.lastRequest.put(this);
        };

        db.open();

        // clear out any collection data if there is a reset parameter
        if($routeParams.delete) {
            db.delete().then(function() {
                // redirect to this page without the parameter
                window.location.href = '/#/dashboard/' + $routeParams.id;
            });
        }

        // remove any data from the existing tables
        if($routeParams.reset || HygieiaConfig.local) {
            db.lastRequest.clear();
            db.codeAnalysis.clear();
            db.testSuite.clear();
            db.buildData.clear();
            db.prodCommit.clear();
        }

        // public properties
        ctrl.stages = ['Commit', 'Build', 'Dev', 'QA', 'Int', 'Perf', 'Prod'];
        ctrl.sortableOptions = {
            additionalPlaceholderClass: 'product-table-tr',
            placeholder: function(el) {
                // create a placeholder row
                var tr = $document[0].createElement('div');
                for(var x=0;x<=ctrl.stages.length;x++) {
                    var td = $document[0].createElement('div');
                    td.setAttribute('class', 'product-table-td');

                    if(x == 0) {
                        var name = $document[0].createElement('div');
                        name.setAttribute('class', 'team-name');
                        name.innerText = el.element[0].querySelector('.team-name').innerText;
                        td.setAttribute('class', 'product-table-td team-name-cell');
                        td.appendChild(name);
                    }
                    tr.appendChild(td);
                }

                return tr;
            },
            orderChanged: function() {
                var teams = ctrl.configuredTeams,
                    existingConfigTeams = $scope.widgetConfig.options.teams,
                    newConfigTeams = [];

                _(teams).forEach(function(team) {
                    _(existingConfigTeams).forEach(function(configTeam) {
                        if(team.collectorItemId == configTeam.collectorItemId) {
                            newConfigTeams.push(configTeam);
                        }
                    });
                });

                $scope.widgetConfig.options.teams = newConfigTeams;
                updateWidgetOptions($scope.widgetConfig.options);
            }
        };

        // public methods
        ctrl.load = load;
        ctrl.addTeam = addTeam;
        ctrl.editTeam = editTeam;
        ctrl.openDashboard = openDashboard;
        ctrl.viewTeamStageDetails = viewTeamStageDetails;
        ctrl.viewQualityDetails = viewQualityDetails;

        // public data methods
        ctrl.teamStageHasCommits = teamStageHasCommits;

        // set our data before we get things started
        var widgetOptions = angular.copy($scope.widgetConfig.options);

        if (widgetOptions && widgetOptions.teams) {
            ctrl.configuredTeams = widgetOptions.teams;
        }

        //region public method implementations
        function load() {
            // determine our current state
            if (isReload === null) {
                isReload = false;
            }
            else if(isReload === false) {
                isReload = true;
            }

            collectTeamStageData(widgetOptions.teams, [].concat(ctrl.stages));

            var requestedData = getTeamDashboardDetails(widgetOptions.teams);
            if(!requestedData) {
                for(var collectorItemId in teamDashboardDetails) {
                    getTeamComponentData(collectorItemId);
                }
            }
        }

        // remove data from the db where data is older than the provided timestamp
        function cleanseData(table, beforeTimestamp) {
            table.where('timestamp').below(beforeTimestamp).toArray(function(rows) {
                _(rows).forEach(function(row) {
                    table.delete(row.id);
                })
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

        function editTeam(collectorItemId) {
            var team = false;
            _($scope.widgetConfig.options.teams)
                .filter({collectorItemId: collectorItemId})
                .forEach(function(t) {
                    team = t;
                });

            if(!team) { return; }

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

                var newOptions = $scope.widgetConfig.options;

                // take the collector item out of the team array
                if(config.remove) {
                    // do remove
                    var keepTeams = [];

                    _(newOptions.teams).forEach(function(team) {
                        if(team.collectorItemId != config.collectorItemId) {
                            keepTeams.push(team);
                        }
                    });

                    newOptions.teams = keepTeams;
                }
                else {
                    for(var x=0;x<newOptions.teams.length;x++) {
                        if(newOptions.teams[x].collectorItemId == config.collectorItemId) {
                            newOptions.teams[x] = config;
                        }
                    }
                }

                updateWidgetOptions(newOptions);
            });
        }

        function openDashboard(item) {
            var dashboardDetails = teamDashboardDetails[item.collectorItemId];
            if(dashboardDetails) {
                $location.path('/dashboard/' + dashboardDetails.id);
            }
        }

        function viewTeamStageDetails(team, stage) {
            // only show details if we have commits
            if(!teamStageHasCommits(team, stage)) {
                return false;
            }

            $modal.open({
                templateUrl: 'components/widgets/product/environment-commits/environment-commits.html',
                controller: 'productEnvironmentCommitController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    modalData: function() {
                        return {
                            team: team,
                            stage: stage,
                            stages: ctrl.stages
                        };
                    }
                }
            });
        }

        function viewQualityDetails(team, stage, metricIndex) {
            $modal.open({
                templateUrl: 'components/widgets/product/quality-details/quality-details.html',
                controller: 'productQualityDetailsController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    modalData: function() {
                        return {
                            team: team,
                            stage: stage,
                            metricIndex: metricIndex
                        }
                    }
                }
            })
        }
        //endregion

        //region public data method implementations
        function setTeamData(collectorItemId, data) {
            var team = false,
                idx = false;

            _(ctrl.configuredTeams).forEach(function(configuredTeam, i) {
                if(configuredTeam.collectorItemId == collectorItemId) {
                    idx = i;
                    team = configuredTeam;
                }
            });

            if(!team) { return; }

            var obj = ctrl.configuredTeams[idx];

            // hackish way to update the configured teams object in place so their entire
            // object does not need to be replaced which would cause a full refresh of the
            // row instead of just the numbers. some deep merge tools did not replace everything
            // correctly so this way we can be explicit in the behavior
            for(var x in data) {
                var xData = data[x];
                if(typeof xData == 'object' && obj[x] != undefined) {
                    for(var y in xData) {
                        var yData = xData[y];

                        if(typeof yData == 'object' && obj[x][y] != undefined) {
                            for (var z in yData) {
                                var zData = yData[z];
                                obj[x][y][z] = zData;
                            }
                        }
                        else {
                            obj[x][y] = yData;
                        }
                    }
                }
                else {
                    obj[x] = xData;
                }
            }
        }

        function getTeamDashboardDetails(teams) {
            var update = false;
            _(teams).forEach(function(team) {
                if(!teamDashboardDetails[team.collectorItemId]) {
                    update = true;
                }
            });

            // if we already have all the teams, don't make the call
            if (!update) {
                return false;
            }

            // let's grab our products and update all the board info
            collectorData.itemsByType('product').then(function(response) {
                _(teams).forEach(function(team) {
                    _(response).forEach(function(board) {
                        if (team.collectorItemId == board.id) {
                            dashboardData.detail(board.options.dashboardId).then(function(result) {
                                teamDashboardDetails[team.collectorItemId] = result;

                                getTeamComponentData(team.collectorItemId);
                            });
                        }
                    });
                });
            });

            return true;
        }

        function getTeamComponentData(collectorItemId) {
            var team = teamDashboardDetails[collectorItemId],
                componentId = team.application.components[0].id;

            function getCaMetric(metrics, name, fallback) {
                var val = fallback === undefined ? false : fallback;
                _(metrics).filter({name:name}).forEach(function(item) {
                    val = item.value || parseFloat(item.formattedValue);
                });
                return val;
            }

            // region Build Data
            db.lastRequest.where('[type+id]').equals(['build-data', componentId]).first()
                .then(function(lastRequest) {
                    var now = moment(),
                        dateEnds = now.valueOf(),
                        ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                        dateBegins = ninetyDaysAgo;

                    // if we already have made a request, just get the delta
                    if(lastRequest) {
                        dateBegins = lastRequest.timestamp;
                    }

                    buildData
                        .details({componentId: componentId, endDateBegins: dateBegins, endDateEnds: dateEnds})
                        .then(function(response) {
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
                        })
                        .then(function() {
                            db.buildData.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function(rows) {
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
                                    setTeamData(collectorItemId, buildData);
                                });
                            });
                        })
                        .finally(function() {
                            cleanseData(db.buildData, ninetyDaysAgo);
                        });
                });
            // endregion

            // region Security Analysis
            // get our security analysis data. start by seeing if we've already run this request
            db.lastRequest.where('[type+id]').equals(['security-analysis', componentId]).first()
                .then(function(lastRequest) {
                    var now = moment(),
                        dateEnds = now.valueOf(),
                        ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                        dateBegins = ninetyDaysAgo;

                    // if we already have made a request, just get the delta
                    if(lastRequest) {
                        dateBegins = lastRequest.timestamp;
                    }

                    codeAnalysisData
                        .securityDetails({componentId: componentId, dateBegins: dateBegins, dateEnds: dateEnds})
                        .then(function(response) {
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
                                    type: 'security-analysis',
                                    timestamp: dateEnds
                                });
                            }

                            // put all results in the database
                            _(response.result).forEach(function(result) {
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
                        .then(function() {
                            db.securityAnalysis.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function(rows) {
                                if(!rows.length) {
                                    return;
                                }

                                // make sure it's sorted with the most recent first (largest timestamp)
                                rows = _(rows).sortBy('timestamp').reverse().value();

                                // prepare the data for the regression test mapping days ago on the x axis
                                var now = moment(),
                                    securityIssues = _(rows).map(function(row) {
                                        var daysAgo = -1 * moment.duration(now.diff(row.timestamp)).asDays();
                                        return [daysAgo, row.major + row.critical + row.blocker];
                                    }).value();

                                var securityIssuesResult = regression('linear', securityIssues),
                                    securityIssuesTrendUp = securityIssuesResult.equation[0] > 0;


                                // get the most recent record for current metric
                                var latestResult = rows[0];

                                // use $timeout so that it will apply on the next digest
                                $timeout(function() {
                                    // update data for the UI
                                    setTeamData(collectorItemId, {
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
                        .finally(function() {
                            cleanseData(db.securityAnalysis, ninetyDaysAgo);
                        });
                });

            // endregion

            // region Code Analysis
            // get our code analysis data. start by seeing if we've already run this request
            db.lastRequest.where('[type+id]').equals(['code-analysis', componentId]).first().then(function(lastRequest) {
                var now = moment(),
                    dateEnds = now.valueOf(),
                    ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                    dateBegins = ninetyDaysAgo;

                // if we already have made a request, just get the delta
                if(lastRequest) {
                    dateBegins = lastRequest.timestamp;
                }

                // request our data
                codeAnalysisData
                    .staticDetails({componentId: componentId, dateBegins: dateBegins, dateEnds: dateEnds})
                    .then(function(response) {
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
                    })
                    .then(function() {
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
                                setTeamData(collectorItemId, {
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
                    })
                    .finally(function() {
                        cleanseData(db.codeAnalysis, ninetyDaysAgo);
                    });
            });
            // endregion

            // region Test Suite
            db.lastRequest.where('[type+id]').equals(['test-suite', componentId]).first().then(function(lastRequest) {
                var now = moment(),
                    dateEnds = now.valueOf(),
                    ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                    dateBegins = ninetyDaysAgo;

                // if we already have made a request, just get the delta
                if(lastRequest) {
                    dateBegins = lastRequest.timestamp;
                }

                testSuiteData
                    .details({componentId: componentId, endDateBegins:dateBegins, endDateEnds:dateEnds, depth: 1})
                    .then(function(response) {
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
                                type: 'test-suite',
                                timestamp: dateEnds
                            });
                        }

                        // put all results in the database
                        _(response.result).forEach(function(result) {
                            var totalPassed = 0,
                                totalTests = 0;

                            _(result.testCapabilities).forEach(function(capabilityResult) {
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
                    .then(function() {
                        // now that all the delta data has been saved, request
                        // and process 90 days worth of it
                        db.testSuite.where('[componentId+timestamp]').between([componentId, ninetyDaysAgo], [componentId, dateEnds]).toArray(function(rows) {
                            if (!rows.length) {
                                return;
                            }

                            // make sure it's sorted with the most recent first (largest timestamp)
                            rows = _(rows).sortBy('timestamp').reverse().value();

                            // prepare the data for the regression test mapping days ago on the x axis
                            var now = moment(),
                                data = _(rows).map(function(result) {
                                    var daysAgo = -1 * moment.duration(now.diff(result.timestamp)).asDays(),
                                        totalPassed = result.successCount || 0,
                                        totalTests = result.totalCount,
                                        percentPassed = totalTests ? totalPassed/totalTests : 0;

                                    return [daysAgo, percentPassed];
                                }).value();

                            var passedPercentResult = regression('linear', data),
                                passedPercentTrendUp = passedPercentResult.equation[0] > 0;

                            // get the most recent record for current metric for each collectorItem id
                            var lastRunResults = _(rows).groupBy('collectorItemId').map(function(items, collectorItemId) {
                                var lastRun = _(items).sortBy('timestamp').reverse().first();

                                return {
                                    success: lastRun.successCount || 0,
                                    total: lastRun.totalCount || 0
                                }
                            });

                            var totalSuccess = lastRunResults.pluck('success').reduce(function(a,b) { return a + b }),
                                totalResults = lastRunResults.pluck('total').reduce(function(a,b) { return a + b });

                            // use $timeout so that it will apply on the next digest
                            $timeout(function() {
                                // update data for the UI
                                setTeamData(collectorItemId, {
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
                    .finally(function() {
                        cleanseData(db.testSuite, ninetyDaysAgo);
                    });
            });
            // endregion
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

            $scope.upsertWidget(data);
        }

        // return whether this stage has commits. used to determine whether details
        // will be shown for this team in the specific stage
        function teamStageHasCommits(team, stage) {
            return team.stages && team.stages[stage] && team.stages[stage].commits && team.stages[stage].commits.length;
        }

        function getStageDurationStats(a) {
            var r = {mean: 0, variance: 0, deviation: 0}, t = a.length;
            for(var m, s = 0, l = t; l--; s += a[l]);
            for(m = r.mean = s / t, l = t, s = 0; l--; s += Math.pow(a[l] - m, 2));
            return r.deviation = Math.sqrt(r.variance = s / t), r;
        }

        function collectTeamStageData(teams, ctrlStages) {
            var now = moment(),
                nowTimestamp = now.valueOf(),
                start = now.subtract(90, 'days').valueOf();

            // no need to go further if teams aren't configured
            if(!teams.length) {
                return;
            }

            // region Pipeline Commit Data
            // loop through each team and request pipeline data
            _(teams).forEach(function(configuredTeam) {
                // querying pipeline commits by date will only return production commits that have
                // moved to prod since that last request. this way we can avoid sending 90 days
                // of production commit data with each request. all other environments will show
                // a current snapshot of data
                var collectorItemId = configuredTeam.collectorItemId;


                // get our pipeline commit data. start by seeing if we've already run this request
                db.lastRequest.where('[type+id]').equals(['pipeline-commit', collectorItemId]).first()
                    .then(function(lastRequest) {
                        var now = moment(),
                            dateEnds = now.valueOf(),
                            ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                            dateBegins = ninetyDaysAgo;

                        // if we already have made a request, just get the delta
                        if(lastRequest) {
                            dateBegins = lastRequest.timestamp;
                        }

                        pipelineData
                            .commits(dateBegins, nowTimestamp, collectorItemId)
                            .then(function(response) {
                                if(!response.length) {
                                    return $q.reject('No response found');
                                }

                                // we only requested one team so it's safe to assume
                                // that it's in the first position
                                response = response[0];

                                // don't continue saving local data
                                if(HygieiaConfig.local && isReload) { return response; }

                                // save the request object so we can get the delta next time as well
                                if(lastRequest) {
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
                                _(response.stages.Prod).forEach(function(commit) {
                                    // extend the commit object with fields we need
                                    // to search the db
                                    commit.collectorItemId = collectorItemId;
                                    commit.timestamp = commit.processedTimestamps.Prod;

                                    db.prodCommit.add(commit);
                                });

                                return response;
                            })
                            .then(function(team) {
                                db.prodCommit.where('[collectorItemId+timestamp]').between([collectorItemId, ninetyDaysAgo], [collectorItemId, dateEnds]).toArray(function(rows) {
                                    team.stages.Prod = _(rows).sortBy('timestamp').reverse().value();

                                    var teamStageData = {},
                                        stageDurations = {},
                                        stages = [].concat(ctrlStages); // create a local copy so it doesn't get overwritten

                                    // go backward through the stages and define commit data.
                                    // reverse should make it easier to calculate time in the previous stage
                                    _(stages).reverse().forEach(function(currentStageName) {
                                        var commits = [], // store our new commit object
                                            localStages = [].concat(ctrlStages), // create a copy of the stages
                                            previousStages = _(localStages.splice(0, localStages.indexOf(currentStageName))).reverse().value(); // only look for stages before this one

                                        // loop through each commit and create our own custom commit object
                                        _(team.stages[currentStageName]).forEach(function(commitObj) {
                                            var commit = {
                                                author: commitObj.scmAuthor || 'NA',
                                                message: commitObj.scmCommitLog || 'No message',
                                                id: commitObj.scmRevisionNumber,
                                                timestamp: commitObj.scmCommitTimestamp,
                                                in: {} //placeholder for stage duration data per commit
                                            };

                                            // make sure this stage exists to track durations
                                            if(!stageDurations[currentStageName]) {
                                                stageDurations[currentStageName] = [];
                                            }

                                            // use this commit to calculate time in the current stage
                                            var currentStageTimestampCompare = commit.timestamp;
                                            if(commitObj.processedTimestamps[currentStageName])
                                            {
                                                currentStageTimestampCompare = commitObj.processedTimestamps[currentStageName];
                                            }

                                            // use this time in our metric calculations
                                            var timeInCurrentStage = nowTimestamp - currentStageTimestampCompare;
                                            stageDurations[currentStageName].push(timeInCurrentStage);

                                            // make sure current stage is set
                                            commit.in[currentStageName] = timeInCurrentStage;

                                            // on each commit, set data for how long it was in each stage by looping
                                            // through any previous stage and subtracting its timestamp from the next stage
                                            var currentStageTimestamp = commitObj.processedTimestamps[currentStageName];
                                            _(previousStages).forEach(function(previousStage) {
                                                if(!commitObj.processedTimestamps[previousStage] || isNaN(currentStageTimestamp)) {
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
                                                if(!stageDurations[previousStage]) {
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
                                    });

                                    // now that we've added all the duration data for all commits in each stage
                                    // we can calculate the averages and std deviation and put the data on the stage
                                    _(stageDurations).forEach(function(durationArray, currentStageName) {
                                        if(!teamStageData[currentStageName]) {
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
                                    _(teamStageData).forEach(function(data, stage) {

                                        if(!data.stageStdDeviation || !data.commits) {
                                            return;
                                        }

                                        _(data.commits).forEach(function(commit) {
                                            // use the time it's been in the existing environment to compare
                                            var timeInStage = commit.in[stage];

                                            commit.errorState = timeInStage > 2 * data.stageStdDeviation;
                                        });
                                    });

                                    // create some summary data used in each stage's cell
                                    _(teamStageData).forEach(function(stageData, stageName) {
                                        stageData.summary = {
                                            // helper for determining whether this stage has current commits
                                            hasCommits: stageData.commits && stageData.commits.length ? true : false,

                                            // green block count
                                            commitsInsideTimeframe: _(stageData.commits).filter(function(c) { return !c.errorState; }).value().length,

                                            // red block count
                                            commitsOutsideTimeframe: _(stageData.commits).filter({errorState:true}).value().length,

                                            // stage last updated text
                                            lastUpdated: (function(stageData) {
                                                if(!stageData.commits || !stageData.commits.length) {
                                                    return false;
                                                }

                                                // try to get the last commit to enter this stage by evaluating the duration
                                                // for this current stage, otherwise use the commit timestamp
                                                var lastUpdatedDuration = _(stageData.commits).map(function(commit) {
                                                        return commit.in[stageName] || moment().valueOf() - commit.timestamp;
                                                    }).min().value(),
                                                    lastUpdated = moment().add(-1*lastUpdatedDuration, 'milliseconds');

                                                return {
                                                    longDisplay: lastUpdated.format('MMMM Do YYYY, h:mm:ss a'),
                                                    shortDisplay: lastUpdated.dash('ago')
                                                }
                                            })(stageData),

                                            // stage deviation
                                            deviation: (function(stageData) {
                                                if(!stageData.stageStdDeviation) {
                                                    return false;
                                                }

                                                // determine how to display the standard deviation
                                                var number = moment.duration(stageData.stageStdDeviation).minutes(),
                                                    desc = 'min';

                                                if(number > 60*24) {
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

                                            average: (function(stageData) {
                                                // determine how to display the average time
                                                if(!stageData.stageAverageTime) {
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
                                    var teamProdData = {
                                        averageDays: '--',
                                        totalCommits: 0
                                    },
                                        commitTimeToProd = _(team.stages)
                                            // limit to prod
                                            .filter(function(val, key) {
                                                return key == 'Prod'
                                            })
                                            // make all commits a single array
                                            .reduce(function(num, commits){ return num + commits; })
                                            // they should, but make sure the commits have a prod timestamp
                                            .filter(function(commit) {
                                                return commit.processedTimestamps && commit.processedTimestamps['Prod'];
                                            })
                                            // calculate their time to prod
                                            .map(function(commit) {
                                                return {
                                                    duration: commit.processedTimestamps['Prod'] - commit.scmCommitTimestamp,
                                                    commitTimestamp: commit.scmCommitTimestamp
                                                };
                                            });


                                    teamProdData.totalCommits = commitTimeToProd.length;

                                    if (commitTimeToProd.length > 1) {
                                        var averageDuration = _(commitTimeToProd).pluck('duration').reduce(function(a,b) {
                                            return a + b;
                                        }) / commitTimeToProd.length;

                                        teamProdData.averageDays = Math.floor(moment.duration(averageDuration).asDays());

                                        var plotData = _(commitTimeToProd).map(function(ttp) {
                                            var daysAgo = -1*moment.duration(moment().diff(ttp.commitTimestamp)).asDays();
                                            return [daysAgo, ttp.duration];
                                        }).value();

                                        var averageToProdResult = regression('linear', plotData);
                                        teamProdData.trendUp = averageToProdResult.equation[0] > 0;
                                    }

                                    // handle the api telling us which stages need configuration
                                    if(team.unmappedStages)
                                    {
                                        for(var stageName in teamStageData) {
                                            teamStageData[stageName].needsConfiguration = team.unmappedStages.indexOf(stageName) != -1;
                                        }
                                    }

                                    setTeamData(team.collectorItemId, {
                                        stages: teamStageData,
                                        prod: teamProdData
                                    });
                                });
                            })
                            .finally(function() {
                                cleanseData(db.prodCommit, ninetyDaysAgo);
                            });
                    });
            });
            // endregion
        }
        //endregion
    }
})();
