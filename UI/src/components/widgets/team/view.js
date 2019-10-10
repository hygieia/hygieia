(function() {
    'use strict';
    angular.module(HygieiaConfig.module)
        .controller('TeamViewController', TeamViewController);
    TeamViewController.$inject = ['$q', '$scope', '$uibModal', 'collectorData','teamInventoryData'];

    function TeamViewController($q, $scope, $uibModal, collectorData,teamInventoryData) {
        var ctrl = this;

        ctrl.teamTitle = $scope.widgetConfig.options.teamName;
        ctrl.lastUpdated = "";
        ctrl.load = function() {
            //start getting the response data using http service
            var deferred = $q.defer();
            var params = {
                componentId: $scope.widgetConfig.componentId,
                teamId: $scope.widgetConfig.options.teamId,
                teamName: $scope.widgetConfig.options.teamName,
            };
            teamInventoryData.details(params)
                .then(function(data) {
                    processResponse(data);
                })
                .then(function() {
                    collectorData.getCollectorItem($scope.widgetConfig.componentId, 'TEAM')
                        .then(function(data) {
                            deferred.resolve({
                                lastUpdated: ctrl.lastUpdated,
                                collectorItem: data
                            });
                        });
                });

            return deferred.promise;
            //end getting the response data using http service
        };

        function processResponse(data){
            var teamCount = data.result.teamMembers;
            ctrl.assocs =  0;
            ctrl.nonasscos =  0;
            _.each(teamCount).forEach(function(t){
                if(t.regOrTemp=="R") ctrl.assocs++;
                else ctrl.nonasscos++;
            })
            ctrl.headcount =  {series: [  ctrl.assocs , ctrl.nonasscos ]};
     }


    }
})();
