(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('performanceViewController', performanceViewController);

    performanceViewController.$inject = ['$q', '$scope','performanceData', '$modal'];
    function performanceViewController($q, $scope, performanceData, $modal) {
        var ctrl = this;

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
