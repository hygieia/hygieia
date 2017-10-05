(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productViewController', productViewController)
        .filter('flattenToArray', function() { return function(obj) {
            if (!(obj instanceof Object)) return obj;
            return Object.keys(obj).map(function (key) { return obj[key]; });
        }});


    productViewController.$inject = ['$scope', '$document', '$uibModal', '$location', '$q', '$stateParams', '$timeout', 'buildData', 'codeAnalysisData', 'collectorData', 'dashboardData', 'pipelineData', 'testSuiteData', 'productBuildData', 'productCodeAnalysisData', 'productCommitData', 'productSecurityAnalysisData', 'productTestSuiteData', 'cicdGatesData'];
    function productViewController($scope, $document, $uibModal, $location, $q, $stateParams, $timeout, buildData, codeAnalysisData, collectorData, dashboardData, pipelineData, testSuiteData, productBuildData, productCodeAnalysisData, productCommitData, productSecurityAnalysisData, productTestSuiteData, cicdGatesData) {
        /*jshint validthis:true */
        var ctrl = this;

        //region Dexie configuration
        // setup our local db
        var db = new Dexie('ProductPipelineDb');
        Dexie.Promise.on('error', function(err) {
            // Log to console or show en error indicator somewhere in your GUI...
            console.log('Uncaught Dexie error: ' + err);
        });

        // IMPORTANT: when updating schemas be sure to version the database
        // https://github.com/dfahlander/Dexie.js/wiki/Design#database-versioning
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
        if($stateParams.delete) {
            db.delete().then(function() {
                // redirect to this page without the parameter
                window.location.href = '/#/dashboard/' + $stateParams.id;
            });
        }

        // remove any data from the existing tables
        if($stateParams.reset || HygieiaConfig.local) {
            db.lastRequest.clear();
            db.codeAnalysis.clear();
            db.testSuite.clear();
            db.buildData.clear();
            db.prodCommit.clear();
        }
        // endregion

        // private properties
        var teamDashboardDetails = {},
            isReload = null;

        // set our data before we get things started
        var widgetOptions = angular.copy($scope.widgetConfig.options);

        if (widgetOptions && widgetOptions.teams) {
            ctrl.configuredTeams = widgetOptions.teams;
        }

        // tabs to switch between product dashboard and gamification dashboard
        ctrl.tabs = [
            { name: "Dashboard" },
            { name: "Gamification" }
        ];

        ctrl.teamCrlStages = {};
        ctrl.prodStages={};
        ctrl.orderedStages = {};
        ctrl.scoreBoardData = {};
        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.scoreDetails = {};

        // method to toggle tabs
        function toggleView(index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
            if (ctrl.tabs[index].name == "Gamification") {
                ctrl.populateScoreboardData();
            }
        }

        function populateScoreboardData() {
            var teamScoreBoardData = {};

            _(ctrl.configuredTeams).forEach(function(configuredTeam, i) {
                teamScoreBoardData.collectorItemId = configuredTeam.collectorItemId;
                teamScoreBoardData.name = configuredTeam.name;
                teamScoreBoardData.data = {
                    lineCoverage : {
                        value : configuredTeam.data["codeAnalysis"][0].lineCoverage,
                        score : getScoreForMetric("lineCoverage", configuredTeam)
                    },
                    testSuccessDensity : {
                        value : configuredTeam.data["codeAnalysis"][0].testSuccessDensity,
                        score : getScoreForMetric("testSuccessDensity", configuredTeam)
                    }
                };
                var totalScore = 0;
                for(var metricName in teamScoreBoardData.data) {
                    totalScore += teamScoreBoardData.data[metricName].score;
                }
                teamScoreBoardData.totalScore = totalScore;
                ctrl.scoreBoardData[i] = teamScoreBoardData;
                teamScoreBoardData = {};
            });
            console.log("Scoreboard data :", ctrl.scoreBoardData);
        }

        function viewScoreDetails(teamScoreRecord, metricName) {
            $uibModal.open({
                templateUrl: 'components/widgets/product/scoreboard/scoreboard-details.html',
                controller: 'scoreBoardDetailsController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    scoreBoardDetailsConfig: function() {
                        return {
                            teamName: teamScoreRecord.name,
                            metricName: metricName,
                            metricScore: teamScoreRecord.data[metricName].score,
                            metricValue: teamScoreRecord.data[metricName].value
                        }
                    },
                    productViewController: function() {
                        return this
                    }
                }
            });
        }

        function getScoreForMetric(metricName, configuredTeam) {
            var score = 0;
            if(configuredTeam.data == undefined) {
                return false;
            }
            switch(metricName) {
                case "lineCoverage":
                    if(configuredTeam.data.codeAnalysis == undefined) {
                        score = -1;
                    } else {
                        var metricValue = configuredTeam.data.codeAnalysis[0].lineCoverage;
                        if(metricValue < 50) {
                            score = 0;
                        } else if (metricValue >= 50 && metricValue < 60 ) {
                            score = 4;
                        } else if (metricValue >= 60 && metricValue < 70 ) {
                            score = 8;
                        } else if (metricValue >= 70 && metricValue < 80 ) {
                            score = 12;
                        } else if (metricValue >= 80 && metricValue < 90 ) {
                            score = 16;
                        } else if (metricValue >= 90 && metricValue < 95 ) {
                            score = 18;
                        } else if (metricValue >= 95 && metricValue <= 100 ) {
                            score = 20;
                        }
                    }
                    break;
                case "testSuccessDensity":
                    if(configuredTeam.data.codeAnalysis == undefined) {
                        score = -1;
                    } else {
                        var metricValue = configuredTeam.data.codeAnalysis[0].testSuccessDensity;
                        if(metricValue == 100) {
                            score = 20;
                        } else {
                            score = 0;
                        }
                    }
                    break;
            }
            return score;
        }

        // pull all the stages from pipeline. Create a map for all ctrl stages for each team.
        ctrl.load = function() {
            var now = moment(),
                ninetyDaysAgo = now.add(-90, 'days').valueOf(),
                dateBegins = ninetyDaysAgo;
            var nowTimestamp = moment().valueOf();
            // get our pipeline commit data. start by seeing if we've already run this request
            _(ctrl.configuredTeams).forEach(function (configuredTeam) {
                var collectId = configuredTeam.collectorItemId;
                var orderedStages = orderKeys();
                var stages = [];
                pipelineData
                    .commits(dateBegins, nowTimestamp, collectId)
                    .then(function (response) {
                        response = response[0];
                        for (var x in response.stages) {
                            orderedStages.push(x, x);
                        }
                        stages = orderedStages.keys();
                        ctrl.teamCrlStages[collectId] = stages;
                        ctrl.prodStages[collectId] = response.prodStage;
                        ctrl.orderedStages[collectId] = response.orderMap;
                    }).then(processLoad);
            });
        };

        // make ordered list
        function orderKeys() {
            var keys = [];
            var val = {};
            return {
                push: function(k,v){
                    if (!val[k]) keys.push(k);
                    val[k] = v;
                },
                keys: function(){return keys},
                values: function(){return val}
            };
        }


        // public methods
        ctrl.addTeam = addTeam;
        ctrl.editTeam = editTeam;
        ctrl.gamification = gamification;
        ctrl.openDashboard = openDashboard;
        ctrl.viewTeamStageDetails = viewTeamStageDetails;
        ctrl.viewQualityDetails = viewQualityDetails;
        ctrl.viewGatesDetails = viewGatesDetails;
        ctrl.initPerc = initPerc;
        ctrl.toggleView = toggleView;
        ctrl.populateScoreboardData = populateScoreboardData;
        ctrl.viewScoreDetails = viewScoreDetails;

        // public data methods
        ctrl.teamStageHasCommits = teamStageHasCommits;


        //region public methods
        function processLoad() {
            ctrl.sortableOptions = {
                additionalPlaceholderClass: 'product-table-tr',
                placeholder: function(el) {
                    // create a placeholder row
                    var tr = $document[0].createElement('div');
                    for(var x=0;x<=$scope.widgetConfig.options.teams.length+1;x++) {
                        var td = $document[0].createElement('div');
                        td.setAttribute('class', 'product-table-td');

                        if(x == 0) {
                            // add the name of the row so it somewhat resembles the actual data
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
                    // re-order our widget options
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

            // determine our current state
            if (isReload === null) {
                isReload = false;
            }
            else if(isReload === false) {
                isReload = true;
            }

            collectTeamStageData(widgetOptions.teams, [].concat(ctrl.teamCrlStages));

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
            $uibModal.open({
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

                var itemInd = false;

                // iterate over teams and set itemInd to true if team is already added to prod dashboard.
                for(var i=0;i<options.teams.length;i++){
                    if(options.teams[i].collectorItemId == config.collectorItemId){
                        itemInd = true; break;
                    }
                }
                // get team dashboard details and see if build and commit widgets are available
                var dashId = config.dashBoardId;
                var buildInd = false;
                var repoInd = false;
                var widgets=[];
                dashboardData.detail(dashId).then(function(result) {
                    var res = result;
                     widgets = result.widgets;
                    _(widgets).forEach(function (widget) {
                        if(widget.name == "build") buildInd = true;
                        if(widget.name =="repo") repoInd = true;

                    });

                    // prompt a message if team is already added or add to prod dashboard otherwise.
                    if(itemInd){
                        swal(config.name+' dashboard added already');
                    }else if(widgets==null || !buildInd || !repoInd){
                        swal('Configure Build and Code Repository for '+config.name+' before adding to Product Dashboard');
                    }else{
                        // add our new config to the array
                        options.teams.push(config);

                        updateWidgetOptions(options);
                    }
                });
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

            $uibModal.open({
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

        function gamification(configuredTeams) {
            $uibModal.open({
                templateUrl: 'components/widgets/product/gamification/gamification.html',
                controller: 'gamificationController',
                controllerAs: 'ctrl',
                resolve: {
                    gamificationConfig: function() {
                        return {
                            teams: ctrl.configuredTeams
                        }
                    }
                }
            }).result.then(function(config) {
                if(!config) {
                    return;
                }
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

            $uibModal.open({
                templateUrl: 'components/widgets/product/environment-commits/environment-commits.html',
                controller: 'productEnvironmentCommitController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    modalData: function() {
                        return {
                            team: team,
                            stage: stage,
                            stages: ctrl.teamCrlStages[team.collectorItemId]
                        };
                    }
                }
            });
        }

        function viewGatesDetails(team){
            dashboardData.detail(team.dashBoardId).then(function(res){
               var componentId = res.widgets[0].componentId;

            $uibModal.open({
                templateUrl: 'components/widgets/product/cicd-gates/cicd-gates.html',
                controller: 'CicdGatesController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve : {
                    team : function (){
                        return team;
                    },
                    dashboardId : function (){
                      return team.dashBoardId;
                    },
                    componentId: function (){
                      return componentId;
                    }
                }
            })
          })
        }

        function initPerc(team) {
          var name = team.customname || team.name;
          dashboardData.detail(team.dashBoardId).then(function(res) {
            var componentId = res.widgets[0].componentId;
            cicdGatesData.details(name, team.dashBoardId, team.collectorItemId, componentId).then(function(response) {
              var pass = 0;
              for (var i = 0; i < response.length; i++) {
                pass += response[i].value == "pass" ? 1 : 0;
              }
              team.passedGates = pass;
              team.totalGates = response.length;
            });
          })
        };

        function viewQualityDetails(team, stage, metricIndex) {
            $uibModal.open({
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

        //region private methods
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

            _(ctrl.configuredTeams).forEach(function(configuredTeam, i) {
                if(configuredTeam.collectorItemId == collectorItemId) {
                    idx = i;
                    team = configuredTeam;
                }
            });
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

            var processDependencyObject = {
                db: db,
                componentId: componentId,
                collectorItemId: collectorItemId,
                setTeamData: setTeamData,
                cleanseData: cleanseData,
                isReload: isReload,
                $timeout: $timeout,
                $q: $q
            };

            // request and process our data
            productBuildData.process(angular.extend(processDependencyObject, { buildData: buildData }));
            productSecurityAnalysisData.process(angular.extend(processDependencyObject, { codeAnalysisData: codeAnalysisData, getCaMetric: getCaMetric }));
            productCodeAnalysisData.process(angular.extend(processDependencyObject, { codeAnalysisData: codeAnalysisData, getCaMetric: getCaMetric }));
            productTestSuiteData.process(angular.extend(processDependencyObject, { testSuiteData: testSuiteData }));
        }

        function collectTeamStageData(teams, teamCtrlStages) {
            // no need to go further if teams aren't configured
            if(!teams || !teams.length) {
                return;
            }

            var nowTimestamp = moment().valueOf();
            // loop through each team and request pipeline data
            _(teams).forEach(function(configuredTeam) {
                var commitDependencyObject = {
                    db: db,
                    configuredTeam: configuredTeam,
                    nowTimestamp: nowTimestamp,
                    setTeamData: setTeamData,
                    cleanseData: cleanseData,
                    pipelineData: pipelineData,
                    $q: $q,
                    $timeout: $timeout,
                    ctrlStages: ctrl.teamCrlStages[configuredTeam.collectorItemId],
                    prodStageValue:ctrl.prodStages[configuredTeam.collectorItemId]
                };

                productCommitData.process(commitDependencyObject);
            });
        }

        $scope.getLinesCoverageMetric = function(collectorItemId) {
            var linesCoverageScore = 0;
            _(ctrl.configuredTeams).forEach(function(configuredTeam, i) {
                if(configuredTeam.collectorItemId == collectorItemId) {
                    linesCoverageScore = configuredTeam.data.codeAnalysis[0].lineCoverage;
                }
            });
            return linesCoverageScore;
        }
        //endregion

        ctrl.getOffset = function getAxisOffset() {
            var offset = 0;
            ctrl.configuredTeams.forEach(function(configuredTeam, i) {
                if (offset < configuredTeam.name.length)
                    offset = configuredTeam.name.length;
            });
            return offset*10;
        };

        ctrl.buildDurationOptions = {
            // plugins: [
            //     Chartist.plugins.threshold({
            //         threshold: $scope.widgetConfig.options.buildDurationThreshold || 10
            //     }),
            //     Chartist.plugins.gridBoundaries(),
            //     Chartist.plugins.tooltip(),
            //     Chartist.plugins.axisLabels({
            //         stretchFactor: 1.4,
            //         axisX: {
            //             labels: [
            //                 moment().subtract(14, 'days').format('MMM DD'),
            //                 moment().subtract(7, 'days').format('MMM DD'),
            //                 moment().format('MMM DD')
            //             ]
            //         }
            //     })
            // ],
            stackBars: true,
            centerLabels: true,
            horizontalBars: true,
            axisX: {
                labelInterpolationFnc: function(value) {
                    return value === 0 ? 0 : ((Math.round(value * 100) / 100) + '');
                }
            },
            axisY: {
                offset: ctrl.getOffset()
            }
        };

        var tableData = [
            { name: "Mike", score: 9001},
            { name: "Not Mike", score: 0}
        ];

        ctrl.getChartData = function getChartData() {
            var labels = [];
            var series1 = [];
            var series2 = [];

            ctrl.configuredTeams.forEach(function(configuredTeam, i) {
                labels.push(configuredTeam.name);
                series1.push(Math.random() * 100);
                series2.push(Math.random() * 100);

            });

            return {labels: labels, series: [series1, series2] };
        };

        ctrl.chartData = ctrl.getChartData();
    }
})();
