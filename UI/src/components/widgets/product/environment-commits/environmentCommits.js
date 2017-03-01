(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productEnvironmentCommitController', productEnvironmentCommitController);

    productEnvironmentCommitController.$inject = ['modalData', '$uibModalInstance', '$timeout'];
    function productEnvironmentCommitController(modalData, $uibModalInstance, $timeout) {
        /*jshint validthis:true */
        var ctrl = this;

        var stageData = modalData.team.stages[modalData.stage];
        if(!stageData) {
            swal({
                title: "No data",
                text: "Unable to find data for the provided stage",
                type: "error",
                closeOnConfirm: true
            }, function() {
                $uibModalInstance.close();
            });

            return;
        }

        // set data
        ctrl.stages = modalData.stages.slice(0, modalData.stages.length - 1);
        ctrl.displayTeamName = modalData.team.customName || modalData.team.name;
        ctrl.currentStageName = modalData.stage;

        ctrl.commits = _(stageData.commits).sortBy('timestamp').value();
        ctrl.totalCommits = stageData.commits.length;

        ctrl.headingPieData = {
            labels: ['',''],
            series: [
                stageData.summary.commitsInsideTimeframe / ctrl.totalCommits,
                stageData.summary.commitsOutsideTimeframe / ctrl.totalCommits
            ]
        };

        ctrl.headingPieOptions = {
            donut: true,
            donutWidth: 6
        };

        // methods
        ctrl.toggleCommitDetails = toggleCommitDetails;
        ctrl.viewCommitInRepo = viewCommitInRepo;
        ctrl.getCommitDisplayAge = function(commit) {
            return moment(commit.timestamp).dash('ago');
        };
        ctrl.getCommitStageTimeDisplay = function(commit, stage) {
            if(!commit.in || !commit.in[stage]) {
                // it hasn't moved on to the next stage, so show how long it's been in this stage
                return '';
            }

            var time = moment.duration(commit.in[stage]),
                days = time.days(),
                hours = time.hours(),
                minutes = time.minutes();

            if (days > 0) {
                return days + 'd';
            }
            else if (hours > 0) {
                return hours + 'h';
            }
            else if (minutes > 0) {
                return minutes + 'm';
            }

            return '< 0m';
        };

        function toggleCommitDetails(commit) {
            commit.expanded = !commit.expanded;
        }

        function viewCommitInRepo(commit, $event) {
            $event.stopPropagation();
        }
    }
})();