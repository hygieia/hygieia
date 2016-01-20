(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('editTeamController', editTeamController);

    editTeamController.$inject = ['$scope', '$modalInstance', 'collectorData', 'editTeamConfig'];
    function editTeamController($scope, $modalInstance, collectorData, editTeamConfig) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties
        ctrl.submitted = false;
        ctrl.dashboards = [];
        ctrl.modalTitle = editTeamConfig.team ? 'Edit team' : 'Add team';
        ctrl.submitText = editTeamConfig.team ? 'Save this team' : 'Add this team';
        ctrl.dropdownConfig = {
            optionLabel: 'title',
            btnClass: 'btn-input',
            placeholder: 'Select a team dashboard'
        };

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
        // since the id will actually be stored in ctrl.collectorItemId when it matches
        // an item even though the title is displayed this lets us compare the two. When
        // they match it means the dashboard id wasn't in the list
        ctrl.onBlur = function(event) {
            var field = document.addTeamForm.collectorItemId;
            if(!!field.value && field.value == ctrl.collectorItemId) {
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



        // init
        (function() {
            if(editTeamConfig.team) {
                ctrl.alternateName = editTeamConfig.team.alternateName;
                ctrl.collectorItemId = editTeamConfig.team.collectorItemId;
            }

            collectorData.itemsByType('product').then(function(result) {

                // limit to team dashboards
                var boards = [];

                _(result).forEach(function(item) {
                    if(item.description) {
                        boards.push({
                            id: item.id,
                            title: item.description,
                            dashboardId: item.options.dashboardId
                        });

                        // if we are editing a team, try to match text
                        // up with the passed collectorItemId
                        if(ctrl.collectorItemId && ctrl.collectorItemId == item.id) {
                            document.addTeamForm.collectorItemId.value = item.description;
                        }
                    }
                });

                ctrl.dropdownOptions = boards;
                ctrl.dashboards = boards;
            });
        })();

        function submit(valid) {
            ctrl.submitted = true;

            if(valid) {
                // get the normal display name
                var name = 'Unknown';
                _(ctrl.dashboards).forEach(function(item) {
                    if(ctrl.collectorItemId == item.id) {
                        name = item.title;
                    }
                });

                var obj = false;

                if (!!ctrl.collectorItemId) {
                    obj = {
                        collectorItemId: ctrl.collectorItemId,
                        name: name,
                        alternateName: ctrl.alternateName,
                        forCollectorItemId: ctrl.team ? ctrl.team.collectorId : false
                    };
                }

                $modalInstance.close(obj);
            }
        }
    }
})();
