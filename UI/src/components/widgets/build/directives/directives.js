(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module);

    var directives = [
        'buildsPerDay',
        'averageBuildDuration',
        'latestBuilds',
        'totalBuilds'
    ];

    _(directives).forEach(function (name) {
        app.directive(name, function () {
            return {
                restrict: 'E',
                templateUrl: 'components/widgets/build/directives/' + name + '.html'
            };
        });
    });


})();
