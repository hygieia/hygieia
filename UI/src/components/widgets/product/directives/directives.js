(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module);

    var directives = [
        'productBuildStageCell',
        'productTeamNameCell'
    ];

    _(directives).forEach(function (name) {
        app.directive(name, function () {
            name = name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
            return {
                restrict: 'E',
                templateUrl: 'components/widgets/product/directives/' + name + '.html'
            };
        });
    });


})();
