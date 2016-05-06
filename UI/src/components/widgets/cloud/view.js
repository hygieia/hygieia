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
        .controller('CloudWidgetViewController', CloudWidgetViewController)
        .filter('pagination', function() {
            return function(data, start)
            {
                start = +start;
                return data.slice(start);
            };
        });

    CloudWidgetViewController.$inject = ['$scope', 'cloudData'];

    function CloudWidgetViewController($scope, cloudData) {


        //private variables
        var ctrl = this;
        var sortDictionary = {};

        //public variables
        ctrl.awsOverview;
        ctrl.instancesByAccount;
        ctrl.sortType = [];
        ctrl.searchFilter = '';

        ctrl.isDetail = false;
        ctrl.accountNumber = $scope.widgetConfig.options.accountNumber || "";
        ctrl.tagName = $scope.widgetConfig.options.tagName || "";
        ctrl.tagValue = $scope.widgetConfig.options.tagValue || "";

        // pagination
        ctrl.curPage = 0;
        ctrl.pageSize = 8;

        ctrl.tabs = [
            { name: "Overview"},
            { name: "Detail"}
        ];


        ctrl.getDaysToExpiration = function(epochTime) {

            if (epochTime == 0) {
                return 'N/A';
            }

            var epochDate = new Date(epochTime);
            var epochDD = ('0' + epochDate.getDate()).slice(-2);
            var epochMM = ('0' + (epochDate.getMonth() + 1)).slice(-2);
            var epochYYYY = epochDate.getFullYear();
            var imageDate = epochMM + '/'+ epochDD + '/' + epochYYYY;

            //get todays date
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1;
            var yyyy = today.getFullYear();

            if(dd<10) { dd='0'+dd }
            if(mm<10) { mm='0'+mm }
            today = mm+'/'+dd+'/'+yyyy;

            return Math.floor(( Date.parse(imageDate) - Date.parse(today) ) / 86400000);
        };


        ctrl.calculateUtilization = function() {


             if (ctrl.instancesByAccount == undefined) {
             return 'N/A';
             }

             var cnt = ctrl.instancesByAccount.length;

             if (cnt == 0) {
             return 'N/A';
             }

             var total = ctrl.instancesByAccount.reduce(function(sum, currentValue) {
             return sum + currentValue.cpuUtilization;
             }, 0);

             return (total / cnt);
        };

        ctrl.changeSortDirection = function(key) {
            var value = sortDictionary[key];
            if (value == undefined) {
                sortDictionary[key] = "-";
            }
            else {
                sortDictionary[key] = value == "-" ? "+" : "-";
            }

            var changedSortType = [];
            var direction = sortDictionary[key];
            changedSortType.push(direction.toString() + key.toString());

            for (var i = 0; i < ctrl.sortType.length; i++) {
                var item = ctrl.sortType[i];
                if (item.substr(1) != key) {
                    changedSortType.push(item);
                }
            }
            ctrl.sortType = changedSortType;
        };

        ctrl.getSortDirection = function(key) {

            var item = sortDictionary[key];

            if (item == undefined) {
                return "unsorted";
            }

            if (item == "+") {
                return "sort-amount-asc";
            }

            return "sort-amount-desc";
        };


        ctrl.checkImageAgeStatus = function(expirationDate) {
            var difference = ctrl.getDaysToExpiration(expirationDate);
            return difference < 0 ? "fail" : difference >= 0 && difference <= 15 ? "warn" : "pass";
        };


        ctrl.checkNOTTDisabledStatus = function(tags) {

            if (tags == undefined) {
                return false;
            }

            for(var i = 0; i < tags.length; i++) {
                var item = tags[i];
                if (item.name.toUpperCase().includes("NOTT") && item.value.toUpperCase() == "EXCLUDE") {
                    return true;
                }
            }
            return false;
        };

        ctrl.checkMonitoredStatus = function(status) {
            return status ? "pass" : "fail";
        };

        ctrl.checkUtilizationStatus = function(status) {
            return status > 30 ? "pass" : "fail";
        };


        ctrl.load = function () {
            cloudData.getAWSInstancesByAccount(ctrl.accountNumber)
                .then(function(data) {
                    ctrl.instancesByAccount = data;
                });
        };


        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        //tested
        /* ctrl.toggleView = function() {
            ctrl.isDetail = (ctrl.isDetail == false);

            if (ctrl.isDetail) {
                cloudData.getAWSInstancesByAccount(ctrl.accountNumber)
                    .then(function(data) {
                        ctrl.instancesByAccount = data;
                    });
            }
        }; */

        ctrl.numberOfPages = function()  {
            return Math.ceil(ctrl.instancesByAccount.length / ctrl.pageSize);
        };

        ctrl.load();
    }
})();