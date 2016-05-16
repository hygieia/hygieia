(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('IssueClosedViewController', IssueClosedViewController);

    IssueClosedViewController.$inject = ['$q', '$scope','issueRepoData', '$modal'];
    function IssueClosedViewController($q, $scope, issueRepoData, $modal) {
        var ctrl = this;

        ctrl.issueChartOptions = {
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
                            moment().subtract(90, 'days').format('MMM DD'),
                            moment().subtract(45, 'days').format('MMM DD'),
                            moment().format('MMM DD')
                        ]
                    }
                }),
                Chartist.plugins.ctPointLabels({
                    textAnchor: 'middle'
                })
            ],
            showArea: true,
            lineSmooth: false,
            fullWidth: true,
            axisY: {
                offset: 30,
                showGrid: true,
                showLabel: true,
                labelInterpolationFnc: function(value) { return Math.round(value * 100) / 100; }
            }
        };

        ctrl.issues = [];
        ctrl.showDetail = showDetail;
        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 90
            };
            issueRepoData.details(params).then(function(data) {
                processResponse(data.result, params.numberOfDays);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'IssueClosedDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/issuesclosed/detail.html',
                size: 'lg',
                resolve: {
                    issues: function() {
                        return groupedissueData[pointIndex];
                    }
                }
            });
        }

        var groupedissueData = [];
        function processResponse(data, numberOfDays) {
            // get total issues by day
            var issues = [];
            var groups = _(data).sortBy('scmCommitTimestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.scmCommitTimestamp))).asDays());
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

            //update charts
            if(issues.length)
            {
                var labels = [];
                _(issues).forEach(function(c) {
                    labels.push('');
                });

                ctrl.issueChartData = {
                    series: [issues],
                    labels: labels
                };
            }


            // group get total counts and contributors
            var today = toMidnight(new Date());
            var fortyfiveDays = toMidnight(new Date());
            var ninetyDays = toMidnight(new Date());
            fortyfiveDays.setDate(fortyfiveDays.getDate() - 45);
            ninetyDays.setDate(ninetyDays.getDate() - 90);

            var lastDayCount = 0;
            var lastDayContributors = [];

            var lastfortyfiveDayCount = 0;
            var lastfortyfiveDaysContributors = [];

            var lastninetyDayCount = 0;
            var lastninetyDaysContributors = [];

            // loop through and add to counts
            _(data).forEach(function (issue) {

                if(issue.scmCommitTimestamp >= today.getTime()) {
                    lastDayCount++;

                    if(lastDayContributors.indexOf(issue.developerName) == -1) {
                        lastDayContributors.push(issue.developerName);
                    }
                }
                else if(issue.scmCommitTimestamp >= fortyfiveDays.getTime()) {
                    lastfortyfiveDayCount++;

                    if(lastfortyfiveDaysContributors.indexOf(issue.developerName) == -1) {
                        lastfortyfiveDaysContributors.push(issue.developerName);
                    }
                }
                else if(issue.scmCommitTimestamp >= ninetyDays.getTime()) {
                    lastninetyDayCount++;
                    ctrl.issues.push(issue);
                    if(lastninetyDaysContributors.indexOf(issue.developerName) == -1) {
                        lastninetyDaysContributors.push(issue.developerName);
                    }
                }

            });

            ctrl.lastDayIssueCount = lastDayCount;
            ctrl.lastDayContributorCount = lastDayContributors.length;
            ctrl.lastfortyfiveDaysIssueCount = lastfortyfiveDayCount;
            ctrl.lastfortyfiveDaysContributorCount = lastfortyfiveDaysContributors.length;
            ctrl.lastninetyDaysIssueCount = lastninetyDayCount;
            ctrl.lastninetyDaysContributorCount = lastninetyDaysContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }

    }
})();
