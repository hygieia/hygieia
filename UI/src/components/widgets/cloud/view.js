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


<<<<<<< HEAD
        var ctrl = this;

        ctrl.awsOverview;
        ctrl.tag = $scope.widgetConfig.options.tag || "";


        ctrl.load = function () {
            ctrl.awsOverview = cloudData.getAWSGlobalData();
        }


        ctrl.load();

=======

        var ctrl = this;
        ctrl.tag = $scope.widgetConfig.options.tag || "";

        ctrl.load = function() {
            return cloudData.getEC2DataSummarizedByTag(ctrl.tag);
        }


>>>>>>> 7f4dd7b36c6b3f02f3cad54fa8a23342bae9a08c
    }

})();