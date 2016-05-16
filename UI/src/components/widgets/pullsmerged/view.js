(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PullMergedViewController', PullMergedViewController);

    PullMergedViewController.$inject = ['$q', '$scope','pullRepoData', '$modal'];
    function PullMergedViewController($q, $scope, pullRepoData, $modal) {
        var ctrl = this;

        ctrl.pullChartOptions = {
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

        ctrl.pulls = [];
        ctrl.showDetail = showDetail;
        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 90
            };
            pullRepoData.details(params).then(function(data) {
                processResponse(data.result, params.numberOfDays);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'PullDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/pulls/detail.html',
                size: 'lg',
                resolve: {
                    pulls: function() {
                        return groupedpullData[pointIndex];
                    }
                }
            });
        }

        var groupedpullData = [];
        function processResponse(data, numberOfDays) {

            // get total pulls by day
            var pulls = [];
            var groups = _(data).sortBy('scmCommitTimestamp')
                .groupBy(function(item) {
                    return -1 * Math.floor(moment.duration(moment().diff(moment(item.scmCommitTimestamp))).asDays());
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

            //update charts
            if(pulls.length)
            {
                var labels = [];
                _(pulls).forEach(function(c) {
                    labels.push('');
                });

                ctrl.pullChartData = {
                    series: [pulls],
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
            _(data).forEach(function (pull) {

                if(pull.scmCommitTimestamp >= today.getTime()) {
                    lastDayCount++;

                    if(lastDayContributors.indexOf(pull.developerName) == -1) {
                        lastDayContributors.push(pull.developerName);
                    }
                }
                else if(pull.scmCommitTimestamp >= fortyfiveDays.getTime()) {
                    lastfortyfiveDayCount++;

                    if(lastfortyfiveDaysContributors.indexOf(pull.developerName) == -1) {
                        lastfortyfiveDaysContributors.push(pull.developerName);
                    }
                }
                else if(pull.scmCommitTimestamp >= ninetyDays.getTime()) {
                    lastninetyDayCount++;
                    ctrl.pulls.push(pull);
                    if(lastninetyDaysContributors.indexOf(pull.developerName) == -1) {
                        lastninetyDaysContributors.push(pull.developerName);
                    }
                }

            });

            ctrl.lastDayPullCount = lastDayCount;
            ctrl.lastDayContributorCount = lastDayContributors.length;
            ctrl.lastfortyfiveDaysPullCount = lastfortyfiveDayCount;
            ctrl.lastfortyfiveDaysContributorCount = lastfortyfiveDaysContributors.length;
            ctrl.lastninetyDaysPullCount = lastninetyDayCount;
            ctrl.lastninetyDaysContributorCount = lastninetyDaysContributors.length;

            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }

    }
})();
