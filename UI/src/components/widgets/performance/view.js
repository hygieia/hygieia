(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('performanceViewController', performanceViewController);

    performanceViewController.$inject = ['$q', '$scope','performanceData', '$modal'];
    function performanceViewController($q, $scope, performanceData, $modal) {
        var ctrl = this;


        ctrl.callsChartOptions = {
          plugins: [
            Chartist.plugins.gridBoundaries(),
            Chartist.plugins.lineAboveArea(),
            Chartist.plugins.pointHalo(),
            Chartist.plugins.ctPointClick({
              //TODO
            }),
            Chartist.plugins.axisLabels({
              axisX: {
                labels: [

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
            labelInterpolationFnc: function(value) {return Math.round(value * 100)/100;}
          }
        };

        ctrl.calls = 100;

        //ctrl.showDetail = showDetail;
        ctrl.load = function() {
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                app_Id: "blank" //to change dynamically
            };
            performanceData.report(params).then(function(data) {
                processResponse(data);
                ctrl.errors = data.errors;
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        /*function showDetail(evt) {
            var target = evt.target,
                pointIndex = target.getAttribute('ct:point-index');

            $modal.open({
                controller: 'RepoDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/repo/detail.html',
                size: 'lg',
                resolve: {
                    commits: function() {
                        return groupedCommitData[pointIndex];
                    }
                }
            });
        }*/

        var groupedCommitData = [];
        function processResponse(data) {
            //debugger;
            ctrl.responsetime = data.responsetime;
            ctrl.calls = data.calls;
            ctrl.callspm = data.callspm;
            ctrl.errors = data.errors;
            ctrl.errorspc = data.errorspc;
            ctrl.errorspm = data.errorspm;
            ctrl.businesshealth = data.businesshealth;
            ctrl.nodehealth = data.nodehealth;
        }

    }
})();
