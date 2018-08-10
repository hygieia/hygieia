/**
 * Team widget configuration
 */
(function() {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('TeamWidgetConfigController', TeamWidgetConfigController);
    TeamWidgetConfigController.$inject = ['modalData', '$scope','collectorData', '$uibModalInstance'];
    function TeamWidgetConfigController(modalData, $scope, collectorData, $uibModalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        ctrl.submit = submitForm;
        ctrl.getTeamNames = getTeamNames;
        ctrl.onSelectTeam = onSelectTeam;
        loadSavedTeam();

        function getTeamNames(filter) {
            return collectorData.itemsByType('TEAM', {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }

        // method implementations
        function loadSavedTeam(){
            var teamCollector = modalData.dashboard.application.components[0].collectorItems.TEAM,
                savedCollectorItem = teamCollector ? teamCollector[0].id : null;

            if(savedCollectorItem) {

                getTeamById(savedCollectorItem).then(getTeamCallback)
            }
        }

        function getTeamCallback(data) {
            ctrl.teamCollectorItem = data;
        }


        function getTeamById(id) {
            return collectorData.getCollectorItemById(id).then(function (response){
                return response;
            });
        }


        function onSelectTeam(item,form){
            ctrl.selectedTeamObject = item;

        }


        function submitForm(valid) {
            if (valid) {
            var teamCollectorId = ctrl.selectedTeamObject.collectorId;
            var teamId= ctrl.selectedTeamObject.teamId;
            var teamName= ctrl.selectedTeamObject.teamName;
                var postObj = {
                    name: 'TEAM',
                    options: {
                        id: widgetConfig.options.id,
                        teamId:teamId,
                        teamName:teamName
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: ctrl.selectedTeamObject.id
                };
                // pass this new config to the modal closing so it's saved
                $uibModalInstance.close(postObj);
            }
        }
    }
})();
