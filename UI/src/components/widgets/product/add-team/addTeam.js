(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('addTeamController', addTeamController);

    addTeamController.$inject = ['$scope', '$uibModalInstance', 'collectorData', '$timeout'];
    function addTeamController($scope, $uibModalInstance, collectorData, $timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties
        ctrl.dashboards = [];

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

        // this is a workaround to clear out values that are not valid team dashboard names
        ctrl.onBlur = function(form) {
            $timeout(function () {
                if (!ctrl.collectorItemId) {   //the model was not set by the typeahead
                    form.collectorItemId.$setViewValue('');
                    form.collectorItemId.$render();
                }
            }, 250);    //a 250 ms delay should be safe enough
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
            if(valid) {
                // get the normal display name
                var name = 'Unknown';
                var dashBoardId = "";
                _(ctrl.dashboards).forEach(function(item) {
                    if(ctrl.collectorItemId == item.id) {
                        name = item.title;
                        dashBoardId = item.dashboardId;
                    }
                });

                var obj = false;

                if (!!ctrl.collectorItemId) {
                    obj = {
                        collectorItemId: ctrl.collectorItemId,
                        name: name,
                        customName: ctrl.customName,
                        dashBoardId: dashBoardId
                    };
                }

                $uibModalInstance.close(obj);
            }
        }
    }
})();
