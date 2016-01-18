(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('editTeamController', editTeamController);

    editTeamController.$inject = ['$scope', '$modalInstance', 'dashboardData', 'editTeamConfig'];
    function editTeamController($scope, $modalInstance, dashboardData, editTeamConfig) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties
        ctrl.submitted = false;
        ctrl.dashboards = [];
        ctrl.modalTitle = editTeamConfig.team ? 'Edit team' : 'Add team';
        ctrl.submitText = editTeamConfig.team ? 'Save this team' : 'Add this team';

        if(editTeamConfig.team && editTeamConfig.team.alternateName) {
            ctrl.alternateName = editTeamConfig.team.alternateName;
        }

        // only match dashboards with
        ctrl.filterDashboards = function(val) {
            return function(item) {
                if(val && val.length) {
                    val = val.trim();
                }
                return !val || !val.length || item.title.toLowerCase().indexOf(val.toLowerCase()) != -1;
            }
        };

        // if we didn't select a value that matches an item in the list, clear the field.
        // since the id will actually be stored in ctrl.dashboard when it matches
        // an item even though the title is displayed this lets us compare the two. When
        // they match it means the dashboard id wasn't in the list
        ctrl.onBlur = function(event) {
            var field = document.addTeamForm.dashboard;
            if(!!field.value && field.value == ctrl.dashboard) {
                field.value = '';
            }
        };

        // workaround to allow the dashboard to store the id, but display the title
        ctrl.formatLabel = function(model) {
            for (var i=0; i< ctrl.dashboards.length; i++) {
                if (model === ctrl.dashboards[i].id) {
                    return ctrl.dashboards[i].title;
                }
            }
        };

        // public methods
        ctrl.submit = submit;


        // methods
        // get all dashboards
        dashboardData.search().then(function(result) {
            // limit to team dashboards
            var boards = [];
            _(result)
                .filter(function(i) {
                    return i.type != 2;
                })
                .forEach(function(item) {
                    boards.push({
                        id: item.id,
                        title: item.title
                    })
                });

            ctrl.dashboards = boards;

            //if(editTeamConfig.team) {
            //    ctrl.dashboard = editTeamConfig.team;
            //}
        });

        function submit(valid) {
            ctrl.submitted = true;

            if(valid) {
                $modalInstance.close( !!ctrl.dashboard ? {
                    dashboardId: ctrl.dashboard,
                    name: ctrl.alternateName
                } : false);
            }
        }
    }
})();
