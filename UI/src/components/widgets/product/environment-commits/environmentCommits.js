(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('productEnvironmentCommitController', productEnvironmentCommitController);

    productEnvironmentCommitController.$inject = ['$scope', '$modalInstance', 'collectorData', '$timeout'];
    function productEnvironmentCommitController($scope, $modalInstance, collectorData, $timeout) {
        /*jshint validthis:true */
        var ctrl = this;


        ctrl.headingPieData = {
            labels: ['',''],
            series: [
                90, 10
            ]
        };

        ctrl.headingPieOptions = {
            donut: true,
            donutWidth: 6
        };

        ctrl.commits = [
            {
                message: 'Fix to resolve MongoDB',
                failState: true,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            }, {
                message: 'Broke the build accidentally on purpose',
                failState: true,
                user: 'Scott',
                age: '14 minutes ago',
                commitNumber: 'asjklwet9872',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            }, {
                message: 'Some bug fix lkaj sdfklj lkja sd sfjklasdf lkasjkdl fa lkjsdf lkjasdl kjfas lkjdf lkjasdlk fal kd  asdlkjf alksj dfjkl asd kljfjkl asdf jklasjkl df jlaksdfkl jasdf kajkls df',
                failState: false,
                user: 'Dave',
                age: '12 minutes ago',
                commitNumber: '9818919fa987as8979',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 1',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 2',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 3',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 4',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 5',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 6',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 7',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 8',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 9',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            },{
                message: 'Testing 10',
                failState: false,
                user: 'Jane',
                age: '25 minutes ago',
                commitNumber: '98asdf98a',
                environments: [
                    {
                        name: 'commit',
                        value: '14m'
                    },{
                        name: 'build',
                        value: '38m'
                    },{
                        name: 'DEV',
                        value: '2h'
                    },{
                        name: 'QA',
                        value: '8h'
                    },{
                        name: 'INT',
                        value: '14h'
                    },{
                        name: 'PERF'
                    }
                ]
            }
        ];

        // methods
        ctrl.toggleCommitDetails = toggleCommitDetails;
        ctrl.viewCommitInRepo = viewCommitInRepo;




        function toggleCommitDetails(index) {
            ctrl.commits[index].expanded = !ctrl.commits[index].expanded;
        }

        function viewCommitInRepo(commit, $event) {
            alert(commit);
            $event.stopPropagation();
            //window.open(url);
        }
    }
})();