(function () {
    'use strict';

    var app = angular
        .module(HygieiaConfig.module)
        .directive('metricCategory',metricCategory);
<<<<<<< HEAD

    function metricCategory() {
        return {
            restrict: 'E',
            scope: { data: '='},
            templateUrl: 'components/widgets/cloud/directives/metricCategory.html'
        };
    }

})();
=======
>>>>>>> 7f4dd7b36c6b3f02f3cad54fa8a23342bae9a08c

    function metricCategory() {
        return {
            restrict: 'E',
            scope: { data: '='},
            templateUrl: 'components/widgets/cloud/directives/metricCategory.html'
        };
    }

