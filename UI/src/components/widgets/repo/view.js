(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('RepoViewController', RepoViewController);

    RepoViewController.$inject = ['$q', '$scope', 'codeRepoData'];
    function RepoViewController($q, $scope, codeRepoData) {
        var ctrl = this;

        ctrl.commitChartOptions = {
            plugins: [
                Chartist.plugins.gridBoundaries(),
                Chartist.plugins.lineAboveArea(),
                Chartist.plugins.pointHalo(),
                Chartist.plugins.tooltip()
            ],
            showArea: true,
            lineSmooth: false,
            fullWidth: true,
            chartPadding: 7,
            axisY: {
                offset: 30,
                //showGrid: false,
                showLabel: true,
                labelInterpolationFnc: function(value) { return Math.round(value * 100) / 100; }
            }
        };

        ctrl.commitChartData = {
            labels: [],
            series: []
        };

        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 14
            };
            codeRepoData.details(params).then(function(data) {
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        function processResponse(data) {
            // get total commits by day
            var commits = [];
            _(data).sortBy('timestamp')
                .groupBy(function(item) {
                    return moment(item.scmCommitTimestamp).format('L');
                }).forEach(function(group) {
                    commits.push(group.length);
                });

            //update charts
            ctrl.commitChartData.labels = [
                moment().subtract(14, 'days').format('MMM DD'),
                moment().subtract(7, 'days').format('MMM DD'),
                moment().format('MMM DD')
            ];
            ctrl.commitChartData.series = [commits];


            // group get total counts and contributors
            var today = toMidnight(new Date());
            var sevenDays = toMidnight(new Date());
            var fourteenDays = toMidnight(new Date());
            sevenDays.setDate(sevenDays.getDate() - 7);
            fourteenDays.setDate(fourteenDays.getDate() - 14);

            var lastDayCount = 0;
            var lastDayContributors = [];

            var lastSevenDayCount = 0;
            var lastSevenDaysContributors = [];

            var lastFourteenDayCount = 0;
            var lastFourteenDaysContributors = [];

            // loop through and add to counts
            _(data).forEach(function (commit) {
                if(commit.scmCommitTimestamp >= today.getTime()) {
                    lastDayCount++;

                    if(lastDayContributors.indexOf(commit.scmAuthor) == -1) {
                        lastDayContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= sevenDays.getTime()) {
                    lastSevenDayCount++;

                    if(lastSevenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastSevenDaysContributors.push(commit.scmAuthor);
                    }
                }

                if(commit.scmCommitTimestamp >= fourteenDays.getTime()) {
                    lastFourteenDayCount++;

                    if(lastFourteenDaysContributors.indexOf(commit.scmAuthor) == -1) {
                        lastFourteenDaysContributors.push(commit.scmAuthor);
                    }
                }
            });

            ctrl.lastDayCommitCount = lastDayCount;
            ctrl.lastDayContributorCount = lastDayContributors.length;
            ctrl.lastSevenDaysCommitCount = lastSevenDayCount;
            ctrl.lastSevenDaysContributorCount = lastSevenDaysContributors.length;
            ctrl.lastFourteenDaysCommitCount = lastFourteenDayCount;
            ctrl.lastFourteenDaysContributorCount = lastFourteenDaysContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }

        }
    }
})();
