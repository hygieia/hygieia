(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module);

    // simple way to add multiple directives with basic templates so we
    // can break apart the widget
    var directives = {
        productTeamSummaryField : {
            scope: {
                caption: '@caption',
                number: '=number',
                percent: '@percent',
                trendUp: '=trendUp',
                measurement: '@measurement',
                successState: '=successState'
            }
        }
    };

    _(directives).forEach(function (obj, name) {
        app.directive(name, function () {
            name = name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
            obj = angular.extend({
                restrict: 'EA',
                templateUrl: 'components/widgets/product/directives/' + name + '.html'
            }, obj);
            //console.log(obj);
            return obj;
        });
    });


})();
