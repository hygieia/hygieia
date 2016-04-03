(function () {
    'use strict';

    var app = angular
        .module('devops-dashboard');

    var directives = [
        'fullTable',
        'agePieChart',
        'cpuPieChart',
        'notEncryptedPercent',
        'notTaggedPercent',
        'counts'
    ];

    _(directives).forEach(function (name) {
        app.directive(name, function () {
            return {
                restrict: 'E',
                templateUrl: 'components/widgets/cloud/directives/' + name + '.html'
            };
        });
    });


})();
