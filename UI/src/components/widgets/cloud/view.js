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

        //private variables/functions
        var sortDictionary = {};

        ctrl.awsOverview;
        ctrl.instancesByTag;
        ctrl.sortType;

        ctrl.isDetail = false;
        ctrl.tag = $scope.widgetConfig.options.tag || "";


        ctrl.getDaysToExpiration = function(expirationDate) {

            var date = new Date(expirationDate * 1000)
            var expirationDate = date.toLocaleString();

            //get todays date
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1;
            var yyyy = today.getFullYear();

            if(dd<10) { dd='0'+dd }
            if(mm<10) { mm='0'+mm }
            today = mm+'/'+dd+'/'+yyyy;

            return Math.floor(( Date.parse(expirationDate) - Date.parse(today) ) / 86400000);

        }


        ctrl.changeSortDirection = function(key) {
            var value = sortDictionary[key];
            sortDictionary[key] = value == "-" ? "+" : "-";

        }



        ctrl.checkImageAgeStatus = function(expirationDate) {
            var difference = ctrl.getDaysToExpiration(expirationDate);
            return difference < 0 ? "fail" : difference >= 0 && difference <= 15 ? "warn" : "pass";
        }


        ctrl.checkNOTTDisabledStatus = function(tags) {
            for(var i = 0; i < tags.length; i++) {
                var item = tags[i];
                if (item.name.toUpperCase().includes("NOTT") && item.value.toUpperCase() == "EXCLUDE") {
                    return true;
                }
            }
            return false;
        }

        ctrl.checkMonitoredStatus = function(status) {
            return status ? "pass" : "fail";
        }

        ctrl.checkUtilizationStatus = function(status) {
            return status > 30 ? "pass" : "fail";
        }


        ctrl.getSortDirection = function(key) {
            var value = sortDictionary[key];
            if (value == undefined) {
                sortDictionary[key] = "unsorted";
            }

            return sortDictionary[key];
        }


        ctrl.load = function () {
            ctrl.awsOverview = cloudData.getAWSGlobalData();
        }

        ctrl.retrieveInstancesByTag = function(tag, value) {
            ctrl.instancesByTag = cloudData.getAWSInstancesByTag(tag, value);
        }

        ctrl.toggleView = function() {
            ctrl.retrieveInstancesByTag("Tag","Value");
            ctrl.isDetail = (ctrl.isDetail == false);
        }


        ctrl.load();

    }
})();