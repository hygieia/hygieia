/**
 * Created by nmande on 4/12/16.
 * Modified by nmande on 04/27/16
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

        ctrl.checkImageAgeStatus = function(expirationDate) {

            var expirationDate = new Date(expirationDate);

            //get todays date
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1;
            var yyyy = today.getFullYear();

            if(dd<10) { dd='0'+dd }
            if(mm<10) { mm='0'+mm }

            today = mm+'/'+dd+'/'+yyyy;

            var difference = Math.floor(( Date.parse(expirationDate) - Date.parse(today) ) / 86400000);
            return difference < 0 ? "RED" : difference >= 0 && difference <= 15 ? "YELLOW" : "GREEN";
        }


        ctrl.checkNOTTStatus = function(status) {
            return status.toUpperCase() == "EXCLUDED" ? "RED" : "GREEN";
        }

        ctrl.checkMonitoredStatus = function(status) {
            return status ? "GREEN" : "RED";
        }

        ctrl.checkUtilizationStatus = function(status) {
            return status > 30 ? "GREEN" : "RED";
        }


        ctrl.tag = $scope.widgetConfig.options.tag || "";
        ctrl.load = function () {
            ctrl.awsOverview = cloudData.getAWSGlobalData();
        }


        ctrl.load();

    }




})();
