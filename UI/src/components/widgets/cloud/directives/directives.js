(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module)
        .directive('metricCategory',metricCategory);

    function metricCategory() {
        return {
            restrict: 'E',
            scope: { data: '=data'},
            templateUrl: 'components/widgets/cloud/directives/metricCategory.html'
        };
    }

})();
