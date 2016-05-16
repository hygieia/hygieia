(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('GitdeveloperViewController', GitdeveloperViewController);

    GitdeveloperViewController.$inject = ['$q', '$scope','gitdeveloperRepoData', '$modal'];
    function GitdeveloperViewController($q, $scope, gitdeveloperRepoData, $modal) {
        var ctrl = this;

        ctrl.gitdeveloperChartOptions = {
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

        ctrl.gitdevelopers = [];
        ctrl.showDetail = showDetail;
        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                numberOfDays: 90
            };
            gitdeveloperRepoData.details(params).then(function(data) {
                processResponse(data.result, params.numberOfDays);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'GitdeveloperDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/gitdevelopers/detail.html',
                size: 'lg',
                resolve: {
                    gitdevelopers: function() {
                        return groupedgitdeveloperData[pointIndex];
                    }
                }
            });
        }

        var groupedgitdeveloperData = [];
        function processResponse(data, numberOfDays) {
            // get total gitdevelopers by day
            var gitdevelopers = [];
            var orgs = new Map();

            for(var x=0 ; x < data.length; x++) {
                try {
                    var dept = data[x].developerName;
                    if (dept == null)
                        dept = "Unknown";
                    if (orgs.has(dept)) {
                        var y = orgs.get(dept);
                        orgs.set(dept, y + 1);
                    }
                    else {
                        orgs.set(dept, 1);
                    }
                }
                catch (err) {
                    continue;
                }
            }

            var list = [];
            var mapIter = orgs.keys();

            var current;
            while(true) {
                current = mapIter.next();
                if (current.done) {
                    break;
                }
                var k = current.value;
                var d  = {
                    "Key" : current.value,
                    "Value": orgs.get(current.value)
                };
                list.push(d);
            }
            var byValue = list.slice(0);
            byValue.sort(function(a,b) {
                return b.Value - a.Value;
            });
            list=byValue.slice(0,9);
            
            ctrl.TopRankedTotal = data.length;

            if (list[0] != null) {
                ctrl.TopRankedTotal1 = list[0].Value;
                ctrl.TopRanked1 = list[0].Key;
            }
            if (list[1] != null) {
                ctrl.TopRankedTotal2 = list[1].Value;
                ctrl.TopRanked2 = list[1].Key;
            }
            if (list[2] != null) {
                ctrl.TopRankedTotal3 = list[2].Value;
                ctrl.TopRanked3 = list[2].Key;
            }
            if (list[3] != null) {
                ctrl.TopRankedTotal4 = list[3].Value;
                ctrl.TopRanked4 = list[3].Key;
            }
            if (list[4] != null) {
                ctrl.TopRankedTotal5 = list[4].Value;
                ctrl.TopRanked5 = list[4].Key;
            }
            if (list[5] != null) {
                ctrl.TopRankedTotal6 = list[5].Value;
                ctrl.TopRanked6 = list[5].Key;
            }
            if (list[6] != null) {
                ctrl.TopRankedTotal7 = list[6].Value;
                ctrl.TopRanked7 = list[6].Key;
            }
            if (list[7] != null) {
                ctrl.TopRankedTotal8 = list[7].Value;
                ctrl.TopRanked8= list[7].Key;
            }
            if (list[8] != null) {
                ctrl.TopRankedTotal9 = list[8].Value;
                ctrl.TopRanked9 = list[8].Key;
            }
            if (list[9] != null) {
                ctrl.TopRankedTotal10 = list[9].Value;
                ctrl.TopRanked10 = list[0].Key;
            }


            function toMidnight(date) {
                date.setHours(0, 0, 0, 0);
                return date;
            }
        }

    }
})();
