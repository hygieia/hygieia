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


        //private variables/methods
        var ctrl = this;
        var sortDictionary = {};


        var convertEpochTimeToDate = function(epochTime) {
            var epochDate = new Date(epochTime);
            var epochDD = ('0' + epochDate.getDate()).slice(-2);
            var epochMM = ('0' + (epochDate.getMonth() + 1)).slice(-2);
            var epochYYYY = epochDate.getFullYear();
            return epochMM + '/'+ epochDD + '/' + epochYYYY;
        }


        var getTodayDate =  function() {

            //get todays date
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1;
            var yyyy = today.getFullYear();

            if(dd<10) { dd='0'+dd }
            if(mm<10) { mm='0'+mm }
            today = mm+'/'+dd+'/'+yyyy;

            return today;
        };

        var getDaysToExpiration = function(epochTime) {

            if (epochTime == 0) {
                return 'N/A';
            }

            var imageDate = convertEpochTimeToDate(epochTime);
            var today = getTodayDate();

            return Math.floor(( Date.parse(imageDate) - Date.parse(today) ) / 86400000);
        };

        var getNOTTStatus = function(tags) {

            if (tags == undefined) {
                return "enabled";
            }

            for(var i = 0; i < tags.length; i++) {
                var item = tags[i];
                if (item.name.toUpperCase().includes("NOTT") && item.value.toUpperCase() == "EXCLUDE") {
                    return "disabled" ;
                }
            }
            return "enabled";
        };


        //public variables/methods
        ctrl.instancesByAccount;
        ctrl.volumesByAccount;
        ctrl.runningStoppedInstances;
        ctrl.instancesByAge;

        ctrl.accountNumber = $scope.widgetConfig.options.accountNumber || "";
        ctrl.tagName = $scope.widgetConfig.options.tagName || "";
        ctrl.tagValue = $scope.widgetConfig.options.tagValue || "";

        ctrl.tabs = [
            { name: "Overview"},
            { name: "Detail"}
        ];

        ctrl.curPage = 0;
        ctrl.isDetail = false;
        ctrl.pageSize = 8;
        ctrl.sortType = [];
        ctrl.searchFilter = '';
        ctrl.toggledView = ctrl.tabs[0].name;


        ctrl.formatVolume = function bytesToSize(bytes) {
            var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
            if (bytes == 0) return '0 Byte';
            var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
            return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
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


        ctrl.calculateUtilization = function(instances) {
             if (instances == undefined) {
                return 'N/A';
             }

             var cnt = instances.length;

             if (cnt == 0) {
             return 'N/A';
             }

             var total = instances.reduce(function(sum, currentValue) {
                return sum + currentValue.cpuUtilization;
             }, 0);

             return (total / cnt);
        };



        ctrl.calculateVolumeInBytes = function(volumes) {
            if (volumes == undefined) {
                return 'N/A';
            }

            var cnt = volumes.length;

            if (cnt == 0) {
                return 'N/A';
            }

            var total = volumes.reduce(function(sum, currentValue) {
                return sum + currentValue.size;
            }, 0);

            return total * 1073741824;
        };

        ctrl.calculateRunningInstances = function(instances) {
            if (instances == undefined) {
                return 'N/A';
            }

            var cnt = instances.length;

            if (cnt == 0) {
                return 'N/A';
            }

            return instances.filter(function(value) { return (!value.stopped) }).length;

        }

        ctrl.calculateStoppedInstances = function(instances) {
            if (instances == undefined) {
                return 'N/A';
            }

            var cnt = instances.length;

            if (cnt == 0) {
                return 'N/A';
            }

            return instances.filter(function(value) { return (value.stopped) }).length;

        }



        ctrl.calculateCostAverage = function(instances) {
            if (instances == undefined) {
                return 'N/A';
            }

            var cnt = instances.length;

            if (cnt == 0) {
                return 'N/A';
            }

            var total = instances.reduce(function(sum, currentValue) {
                return sum +
                    (currentValue.stopped ? 0 :
                        currentValue.alarmClockStatus == "disabled" ?
                            24 * currentValue.hourlyCost :
                            12 * currentValue.hourlyCost);
            }, 0);
            return (total / cnt);
        }

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


        ctrl.checkImageAgeStatus = function(daysToExpiration) {
            return daysToExpiration < 0 ? "fail" : daysToExpiration >= 0 && daysToExpiration <= 15 ? "warn" : "pass";
        };




        ctrl.checkMonitoredStatus = function(status) {
            return status ? "pass" : "fail";
        };

        ctrl.checkUtilizationStatus = function(status) {
            return status > 30 ? "pass" : "fail";
        };


        ctrl.load = function () {
            cloudData.getAWSInstancesByAccount(ctrl.accountNumber)
                .then(function(instances) {

                    instances.forEach(function(element, index, array) {
                        var daysToExpiration = getDaysToExpiration(element.imageExpirationDate);
                        array[index].daysToExpiration = daysToExpiration;
                    });

                    instances.forEach(function(element, index, array) {
                        var alarmClockStatus = getNOTTStatus(element.tags);
                        array[index].alarmClockStatus = alarmClockStatus;
                    });

                    ctrl.instancesByAccount = instances;



                    var running = ctrl.calculateRunningInstances(instances);
                    var stopped = ctrl.calculateStoppedInstances(instances);
                    ctrl.runningStoppedInstances =  {series: [ running, stopped ]};




                });

            cloudData.getAWSVolumeByAccount(ctrl.accountNumber)
                .then(function(volumes) {
                   ctrl.volumesByAccount = volumes;
                });
        };



        ctrl.toggleView = function (index) {
            ctrl.toggledView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };


        ctrl.numberOfPages = function(length)  {
            return Math.ceil(length/ ctrl.pageSize);
        };

        ctrl.load();
    }
})();