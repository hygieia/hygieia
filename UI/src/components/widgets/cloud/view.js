/**
 * Created by nmande on 4/12/16.
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

        ctrl.awsOverview;

        ctrl.isDetail = false;
        ctrl.toggleView = function() {
            ctrl.isDetail = (ctrl.isDetail == false);
        }

        ctrl.tag = $scope.widgetConfig.options.tag || "";
        ctrl.load = function () {
            ctrl.awsOverview = cloudData.getAWSGlobalData();
        }


        ctrl.load();

    }

})();
