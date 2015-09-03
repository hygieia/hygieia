(function () {
    'use strict';

    var app = angular
        .module('devops-dashboard');

    var directives = [
        'cpuTable',
        'ageTable',
        'encryptedTable'
    ];

    _(directives).forEach(function (name) {
        app.directive(name, function () {
            return {
                restrict: 'E',
                templateUrl: 'components/widgets/cloud/detailDirectives/' + name + '.html'
            };
        });
    });


})();
