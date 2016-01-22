(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('editTeamController', editTeamController);

    editTeamController.$inject = ['$scope', '$modalInstance', 'editTeamConfig'];
    function editTeamController($scope, $modalInstance, editTeamConfig) {
        /*jshint validthis:true */
        var ctrl = this,
            team = editTeamConfig.team;

        ctrl.teamName = team.name;
        ctrl.customName = team.customName;
        if(team.customName) {
            ctrl.customName = team.customName;
        }

        ctrl.removeTeam = removeTeam;
        ctrl.submit = submit;

        function removeTeam() {
            swal({
                title: "Are you sure?",
                text: "This team will be removed from your product pipeline.",
                type: "warning",
                showCancelButton: true,
                cancelButtonText: 'No',
                confirmButtonClass: 'btn-info',
                confirmButtonText: 'Yes, delete it!',
                closeOnConfirm: true
            }, function(){
                $modalInstance.close(angular.extend({
                    remove: true
                }, editTeamConfig.team));
            });
        }

        function submit(valid) {
            if(valid) {
                // get the normal display name
                var name = 'Unknown';
                _(ctrl.dashboards).forEach(function(item) {
                    if(ctrl.collectorItemId == item.id) {
                        name = item.title;
                    }
                });

                team.customName = ctrl.customName;
                $modalInstance.close(team);
            }
        }
    }
})();
