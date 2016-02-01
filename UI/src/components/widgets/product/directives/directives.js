(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module);

    var directives = {
        //productBuildStageCell : {},
        //productTeamNameCell : {},
        productTeamSummaryField : {
            scope: {
                caption: '@caption',
                number: '=number',
                percent: '@percent',
                trendUp: '=trendUp',
                successState: '=successState'
            }
        }
    };

    _(directives).forEach(function (obj, name) {
        app.directive(name, function () {
            name = name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
            obj = angular.extend({
                restrict: 'E',
                templateUrl: 'components/widgets/product/directives/' + name + '.html'
            }, obj);
            console.log(obj);
            return obj;
        });
    });


})();
