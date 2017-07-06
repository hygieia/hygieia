(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoViewController', RepoViewController);

    RepoViewController.$inject = ['$q', '$scope','codeRepoData', 'pullRepoData', 'issueRepoData', 'collectorData', '$uibModal'];
    function RepoViewController($q, $scope, codeRepoData, pullRepoData, issueRepoData, collectorData, $uibModal) {
        var ctrl = this;

        ctrl.combinedChartOptions = {
            plugins: [
                Chartist.plugins.gridBoundaries(),
                Chartist.plugins.lineAboveArea(),
                Chartist.plugins.pointHalo(),
                Chartist.plugins.ctPointClick({
                    onClick: showDetail
                }),
                Chartist.plugins.axisLabels({
                    stretchFactor: 1.4,
                    axisX: {
                        labels: [
                            moment().subtract(14, 'days').format('MMM DD'),
                            moment().subtract(7, 'days').format('MMM DD'),
                            moment().format('MMM DD')
                        ]
                    }
                }),
                Chartist.plugins.ctPointLabels({
                    textAnchor: 'middle'
                })
            ],

            showArea: false,
            lineSmooth: false,
            fullWidth: true,
            axisY: {
                offset: 30,
                showGrid: true,
                showLabel: true,
                labelInterpolationFnc: function (value) {
                    return Math.round(value * 100) / 100;
                }
            }
        };

        ctrl.commits = [];
        ctrl.pulls = [];
        ctrl.issues = [];

        ctrl.showDetail = showDetail;
        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 14
            };

            codeRepoData.details(params).then(function (data) {
                processCommitResponse(data.result, params.numberOfDays);
                ctrl.lastUpdated = data.lastUpdated;
            }).then(function () {
                collectorData.getCollectorItem($scope.widgetConfig.componentId, 'scm').then(function (data) {
                    deferred.resolve( {lastUpdated: ctrl.lastUpdated, collectorItem: data});
                });
            });
            pullRepoData.details(params).then(function (data) {
                processPullResponse(data.result, params.numberOfDays);
                ctrl.lastUpdated = data.lastUpdated;
            }).then(function () {
                collectorData.getCollectorItem($scope.widgetConfig.componentId, 'scm').then(function (data) {
                    deferred.resolve( {lastUpdated: ctrl.lastUpdated, collectorItem: data});
                });
            });
            issueRepoData.details(params).then(function (data) {
                processIssueResponse(data.result, params.numberOfDays);
                ctrl.lastUpdated = data.lastUpdated;
            }).then(function () {
                collectorData.getCollectorItem($scope.widgetConfig.componentId, 'scm').then(function (data) {
                    deferred.resolve( {lastUpdated: ctrl.lastUpdated, collectorItem: data});
                });
            });
            
            return deferred.promise;
        };

        function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            var seriesIndex = target.getAttribute('ct:series-index');

            //alert(ctrl);
            $uibModal.open({
                controller: 'RepoDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/repo/detail.html',
                size: 'lg',

                resolve: {
                    commits: function() {
                        if (seriesIndex == "0")
                            return groupedCommitData[pointIndex];
                    },
                    pulls: function() {
                       if (seriesIndex == "1")
                            return groupedpullData[pointIndex];
                    },
                    issues: function() {
                       if (seriesIndex == "2")
                            return groupedissueData[pointIndex];
                    }
                }
            });
        }

        var commits = [];
        var groupedCommitData = [];
        function processCommitResponse(data, numberOfDays) {
            commits = [];
            groupedCommitData = [];
            // get total commits by day
            var groups = _(data).sortBy('timestamp')
                .groupBy(function (item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.scmCommitTimestamp))).asDays());
                }).value();

            for (var x = -1 * numberOfDays + 1; x <= 0; x++) {
                if (groups[x]) {
                    commits.push(groups[x].length);
                    groupedCommitData.push(groups[x]);
                }
                else {
                    commits.push(0);
                    groupedCommitData.push([]);
                }
            }
            var labels = [];
            _(commits).forEach(function (c) {
                labels.push('');
            });
            //update charts
            if (commits.length)
            {
                ctrl.commitChartData = {
                    series: [commits],
                    labels: labels
                };
            }
            ctrl.combinedChartData = {
                labels: labels,
                series: [{
                    name: 'commits',
                    data: commits
                }, {
                    name: 'pulls',
                    data: pulls
                }, {
                    name: 'issues',
                    data: issues
                }]
            };

            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayCommitCount = 0;
            var lastDayCommitContributors = [];

            var lastSevenDayCommitCount = 0;
            var lastSevenDaysCommitContributors = [];

            var lastFourteenDayCommitCount = 0;
            var lastFourteenDaysCommitContributors = [];

            // loop through and add to counts
            _(data).forEach(function (commit) {

                if(commit.scmCommitTimestamp >= today.getTime()) {
                    lastDayCommitCount++;

                    if(lastDayCommitContributors.indexOf(commit.scmAuthor) == -1) {
                        lastDayCommitContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= sevenDays.getTime()) {
                    lastSevenDayCommitCount++;

                    if(lastSevenDaysCommitContributors.indexOf(commit.scmAuthor) == -1) {
                        lastSevenDaysCommitContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= fourteenDays.getTime()) {
                    lastFourteenDayCommitCount++;
                    ctrl.commits.push(commit);
                    if(lastFourteenDaysCommitContributors.indexOf(commit.scmAuthor) == -1) {
                        lastFourteenDaysCommitContributors.push(commit.scmAuthor);
                    }
                }
            });

            ctrl.lastDayCommitCount = lastDayCommitCount;
            ctrl.lastDayCommitContributorCount = lastDayCommitContributors.length;
            ctrl.lastSevenDaysCommitCount = lastSevenDayCommitCount;
            ctrl.lastSevenDaysCommitContributorCount = lastSevenDaysCommitContributors.length;
            ctrl.lastFourteenDaysCommitCount = lastFourteenDayCommitCount;
            ctrl.lastFourteenDaysCommitContributorCount = lastFourteenDaysCommitContributors.length;


            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }

        var pulls = [];
        var groupedpullData = [];
        function processPullResponse(data, numberOfDays) {
            pulls = [];
            groupedpullData = [];
            // get total pulls by day
            var groups = _(data).sortBy('timestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.timestamp))).asDays());
                }).value();

            for(var x=-1*numberOfDays+1; x <= 0; x++) {
                if(groups[x]) {
                    pulls.push(groups[x].length);
                    groupedpullData.push(groups[x]);
                }
                else {
                    pulls.push(0);
                    groupedpullData.push([]);
                }
            }
            var labels = [];
            _(pulls).forEach(function() {
                labels.push('');
            });
            //update charts
            if(pulls.length)
            {
                ctrl.pullChartData = {
                    series: [pulls],
                    labels: labels
                };

            }
            ctrl.combinedChartData = {
                labels: labels,
                series: [{
                    name: 'commits',
                    data: commits
                }, {
                    name: 'pulls',
                    data: pulls
                }, {
                    name: 'issues',
                    data: issues
                }]
            };

            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayPullCount = 0;
            var lastDayPullContributors = [];

            var lastsevenDayPullCount = 0;
            var lastsevenDaysPullContributors = [];

            var lastfourteenDayPullCount = 0;
            var lastfourteenDaysPullContributors = [];

            // loop through and add to counts
            _(data).forEach(function (pull) {

                if(pull.timestamp >= today.getTime()) {
                    lastDayPullCount++;

                    if(lastDayPullContributors.indexOf(pull.userId) == -1) {
                        lastDayPullContributors.push(pull.userId);
                    }
                }
                else if(pull.timestamp >= sevenDays.getTime()) {
                    lastsevenDayPullCount++;

                    if(lastsevenDaysPullContributors.indexOf(pull.userId) == -1) {
                        lastsevenDaysPullContributors.push(pull.userId);
                    }
                }
                else if(pull.timestamp >= fourteenDays.getTime()) {
                    lastfourteenDayPullCount++;
                    ctrl.pulls.push(pull);
                    if(lastfourteenDaysPullContributors.indexOf(pull.userId) == -1) {
                        lastfourteenDaysPullContributors.push(pull.userId);
                    }
                }

            });

            ctrl.lastDayPullCount = lastDayPullCount;
            ctrl.lastDayPullContributorCount = lastDayPullContributors.length;
            ctrl.lastsevenDaysPullCount = lastsevenDayPullCount;
            ctrl.lastsevenDaysPullContributorCount = lastsevenDaysPullContributors.length;
            ctrl.lastfourteenDaysPullCount = lastfourteenDayPullCount;
            ctrl.lastfourteenDaysPullContributorCount = lastfourteenDaysPullContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }
          
        var issues = [];
        var groupedissueData = [];
        function processIssueResponse(data, numberOfDays) {
            groupedissueData = [];
            issues = [];
            // get total issues by day
            var groups = _(data).sortBy('timestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.timestamp))).asDays());
                }).value();

            for(var x=-1*numberOfDays+1; x <= 0; x++) {
                if(groups[x]) {
                    issues.push(groups[x].length);
                    groupedissueData.push(groups[x]);
                }
                else {
                    issues.push(0);
                    groupedissueData.push([]);
                }
            }
            var labels = [];
            _(issues).forEach(function() {
                labels.push('');
            });
            //update charts
            if(issues.length)
            {
                ctrl.issueChartData = {
                    series: [issues],
                    labels: labels
                };
            }
            ctrl.combinedChartData = {
                labels: labels,
                series: [{
                    name: 'commits',
                    data: commits
                }, {
                    name: 'pulls',
                    data: pulls
                }, {
                    name: 'issues',
                    data: issues
                }]
            };

            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayIssueCount = 0;
            var lastDayIssueContributors = [];

            var lastsevenDayIssueCount = 0;
            var lastsevenDaysIssueContributors = [];

            var lastfourteenDayIssueCount = 0;
            var lastfourteenDaysIssueContributors = [];

            // loop through and add to counts
            _(data).forEach(function (issue) {

                if(issue.timestamp >= today.getTime()) {
                    lastDayIssueCount++;

                    if(lastDayIssueContributors.indexOf(issue.userId) == -1) {
                        lastDayIssueContributors.push(issue.userId);
                    }
                }
                else if(issue.timestamp >= sevenDays.getTime()) {
                    lastsevenDayIssueCount++;

                    if(lastsevenDaysIssueContributors.indexOf(issue.userId) == -1) {
                        lastsevenDaysIssueContributors.push(issue.userId);
                    }
                }
                else if(issue.timestamp >= fourteenDays.getTime()) {
                    lastfourteenDayIssueCount++;
                    ctrl.issues.push(issue);
                    if(lastfourteenDaysIssueContributors.indexOf(issue.userId) == -1) {
                        lastfourteenDaysIssueContributors.push(issue.userId);
                    }
                }

            });

            ctrl.lastDayIssueCount = lastDayIssueCount;
            ctrl.lastDayIssueContributorCount = lastDayIssueContributors.length;
            ctrl.lastsevenDaysIssueCount = lastsevenDayIssueCount;
            ctrl.lastsevenDaysIssueContributorCount = lastsevenDaysIssueContributors.length;
            ctrl.lastfourteenDaysIssueCount = lastfourteenDayIssueCount;
            ctrl.lastfourteenDaysIssueContributorCount = lastfourteenDaysIssueContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }
    }
})();
