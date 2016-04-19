/**
 * Created by hyw912 on 4/12/16.
 */


/**
 * View controller for the build widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CloudWidgetViewController', CloudWidgetViewController);

    CloudWidgetViewController.$inject = ['$scope', 'cloudData'];

    function CloudWidgetViewController($scope, cloudData) {



        var ctrl = this;
        ctrl.tag = $scope.widgetConfig.options.tag || "";

        ctrl.load = function() {
            return cloudData.getEC2Data();
        }


    }
})();