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

    CloudWidgetViewController.$inject = ['$scope', 'cloudData','cloudHistoryData'];

    function CloudWidgetViewController($scope, cloudData, cloudHistoryData) {


        //private variables/methods
        var ctrl = this;
        var sortDictionary = {};


        var convertEpochTimeToDate = function(epochTime) {
            var epochDate = new Date(epochTime);
            var epochDD = ('0' + epochDate.getDate()).slice(-2);
            var epochMM = ('0' + (epochDate.getMonth() + 1)).slice(-2);
            var epochYYYY = epochDate.getFullYear();
            return epochMM + '/'+ epochDD + '/' + epochYYYY;
        };

        var convertEpochTimeToHour = function(epochTime) {
            var epochDate = new Date(epochTime);
            return epochDate.getHours();
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
                if (typeof item.name.toUpperCase().includes=='function') {
                    if (item.name.toUpperCase().includes("NOTT") && item.value.toUpperCase() == "EXCLUDE") {
                        return "disabled" ;
                    }
                } else {
                    if(item.name.toUpperCase().indexOf("NOTT") >= 0  && item.value.toUpperCase() == "EXCLUDE" ) {
                        return "disabled" ;
                    }
                }
            }
            return "enabled";
        };

        var getSubnetStatus = function(usedIPs, availableIPs) {

            if (usedIPs == undefined || availableIPs == undefined) {
                return 'N/A';
            }


            var percentageUsed = usedIPs/(availableIPs + usedIPs);
            return percentageUsed >= .50 ? 'fail' : percentageUsed >= .30 && percentageUsed < .50 ? 'warn' : 'pass';
        };


        //public variables/methods
        ctrl.instancesByAccount;
        ctrl.volumesByAccount;
        ctrl.subnetsByAccount;

        ctrl.filteredInstancesByAccount;
        ctrl.filteredVolumesByAccount;

        ctrl.runningStoppedInstances;
        ctrl.ageOfInstances;

        ctrl.accountNumber = $scope.widgetConfig.options.accountNumber || "";
        ctrl.tagName = $scope.widgetConfig.options.tagName || "";
        ctrl.tagValue = $scope.widgetConfig.options.tagValue || "";

        //UI element management
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

        ctrl.instanceUsageMonthly;
        ctrl.instanceUsageMonthlyLineOptions;
        ctrl.instanceUsageHourly;
        ctrl.instanceUsageHourlyLineOptions;
        ctrl.estimatedMonthlyCharge;
        ctrl.showData = false;

        ctrl.calculateAverageForInterval = function(instances, conversion) {

            var summary  = [];
            var elements = [];

            instances.forEach(function(value) {
                var interval = conversion(value.time);
                if (elements.indexOf(interval) == -1) {
                    elements.push(interval);
                }
            });

            elements.forEach(function(element) {

                var oneInterval = instances.filter(function(value) {
                    return conversion(value.time) ==element;
                });


                var total = oneInterval.reduce(function(sum, currentValue) {
                    return sum + currentValue.total;
                }, 0);

                var cnt = oneInterval.length;

                summary.push({
                    interval: element,
                    avg: (total/cnt)
                })
            });

            return summary;
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
        };

        ctrl.calculateInstancesByAge = function(instances, start, end) {

            if (instances == undefined) {
                return 'N/A';
            }

            var cnt = instances.length;
            if (cnt == 0) {
                return 'N/A';
            }

            if (end == undefined) {
                end = Number.POSITIVE_INFINITY;
            }

            return instances.filter(function(value) { return (value.age >= start && value.age < end) }).length;
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

        };

        ctrl.calculateStoppedInstances = function(instances) {
            if (instances == undefined) {
                return 'N/A';
            }

            var cnt = instances.length;

            if (cnt == 0) {
                return 'N/A';
            }

            return instances.filter(function(value) { return (value.stopped) }).length;

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

        ctrl.formatVolume = function bytesToSize(bytes) {

            if(bytes=='N/A')
            {
                return "N/A";
            }
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

        ctrl.load = function () {

            cloudHistoryData.getInstanceHistoryDataByAccount(ctrl.accountNumber)
                .then(function (instanceDataHistory) {

                    //retrieve cost
                    var latestHistoryEpochTime = Math.max.apply(Math,instanceDataHistory.map(function(value){return value.time;}));
                    var latestCharge = instanceDataHistory.filter(function(data) {
                        return data.time == latestHistoryEpochTime;
                    });
                   ctrl.estimatedMonthlyCharge = latestCharge[0].estimatedCharge;

                    //retrieve instance average
                    var dailyAvg = ctrl.calculateAverageForInterval(instanceDataHistory,convertEpochTimeToDate)
                        .sort(function(first, second) {
                        var firstDate = new Date(first.interval);
                        var secondDate = new Date(second.interval);
                        return  firstDate < secondDate ? -1 :  firstDate > secondDate ? 1 : 0;
                    });

                    var dailySeries = [];
                    var dailyLabels = [];

                    dailyAvg.forEach(function(value) {

                        dailySeries.push({
                            meta: value.interval + " " + Math.round(value.avg),
                            value: Math.round(value.avg)
                        });
                        dailyLabels.push(value.interval.slice(0,5));
                    });

                    ctrl.instanceUsageMonthly = {
                        series : [ dailySeries ] ,
                        labels : dailyLabels
                    };

                    ctrl.instanceUsageMonthlyLineOptions = {
                        plugins: [
                            Chartist.plugins.tooltip(),
                            Chartist.plugins.pointHalo()
                        ],
                        showArea: false,
                        lineSmooth: true,
                        width: 400,
                        height: 190,
                        chartPadding: 7,
                        axisX: {
                            showLabels: true
                        }
                    };

                    //retrieve hourly average
                    var todayEpochTime = new Date(getTodayDate());
                    var todayData = instanceDataHistory.filter(function(value) {
                        return value.time >= todayEpochTime;
                    });

                    var hourlyAvg = ctrl.calculateAverageForInterval(todayData,convertEpochTimeToHour);
                    var hourlyTimeSeries = [];
                    var hourlyTotals = [];

                    hourlyAvg.forEach(function(value){
                        hourlyTimeSeries.push(value.interval);
                        hourlyTotals.push(Math.round(value.avg));
                    })

                    ctrl.instanceUsageHourly = {
                        series: [hourlyTotals],
                        labels : hourlyTimeSeries
                    };

                    ctrl.instanceUsageHourlyLineOptions = {
                        plugins: [
                            Chartist.plugins.gridBoundaries(),
                            Chartist.plugins.lineAboveArea(),
                            Chartist.plugins.tooltip(),
                            Chartist.plugins.pointHalo(),
                            Chartist.plugins.threshold({
                                threshold: 3380
                            })

                        ],
                        showArea: true,
                        lineSmooth: true,
                        fullWidth: true,
                        width: 500,
                        height: 380,
                        chartPadding:10,
                        axisY: {
                            onlyInteger: true,
                        }

                    };
                });

            //retrieve data for the rest of the screen
            cloudData.getAWSSubnetsByAccount(ctrl.accountNumber)
                .then(function(subnets){
                    ctrl.subnetsByAccount = subnets;
                }).then(function() {

                cloudData.getAWSInstancesByAccount(ctrl.accountNumber)
                    .then(function(instances) {

                                instances.forEach(function(element, index, array) {

                                    array[index].daysToExpiration = getDaysToExpiration(element.imageExpirationDate);

                                    array[index].alarmClockStatus = getNOTTStatus(element.tags);

                                    array[index].formattedTags = JSON.stringify(element.tags).split(",").join("<br />");

                                    var subnet;
                                    if(typeof ctrl.subnetsByAccount.find=='function') {
                                        subnet = ctrl.subnetsByAccount.find(function(value) {
                                            return value.subnetId == element.subnetId
                                        });
                                    }

                                    if (subnet != undefined) {
                                        array[index].subnetUsageStatus = getSubnetStatus(subnet.usedIPCount, subnet.availableIPCount);
                                    }
                                });

                                ctrl.instancesByAccount = instances;


                                if (ctrl.tagName != "" && ctrl.tagValue != "") {

                                    ctrl.filteredInstancesByAccount = instances.filter(function(item) {

                                        if (item.tags == undefined) {
                                            return false;
                                        }

                                        return (
                                        item.tags.filter(function(value) {
                                            return (value.name == ctrl.tagName && value.value == ctrl.tagValue);
                                        }).length > 0);
                                    });
                                } else {
                                    ctrl.filteredInstancesByAccount = ctrl.instancesByAccount;
                                }

                                var running = ctrl.calculateRunningInstances(ctrl.instancesByAccount);
                                var stopped = ctrl.calculateStoppedInstances(ctrl.instancesByAccount);
                                ctrl.runningStoppedInstances =  {series: [ running, stopped ]};

                                var lessThan15Days = ctrl.calculateInstancesByAge(ctrl.instancesByAccount,0, 15);
                                var lessThan45Days = ctrl.calculateInstancesByAge(ctrl.instancesByAccount,15, 45);
                                var greaterThan45Days = ctrl.calculateInstancesByAge(ctrl.instancesByAccount,45, undefined);

                                ctrl.ageOfInstances = { series: [ lessThan15Days, lessThan45Days, greaterThan45Days] };
                            }).then(function() {
                            cloudData.getAWSVolumeByAccount(ctrl.accountNumber)
                                .then(function(volumes) {

                                    ctrl.volumesByAccount = volumes;
                                    var volumeList = [];

                                    for (var i = 0; i < ctrl.filteredInstancesByAccount.length; i++) {

                                        var instanceId = ctrl.filteredInstancesByAccount[i].instanceId;
                                        ctrl.volumesByAccount.filter(function(value) {
                                            if (value.attchInstances == undefined) {
                                                return false;
                                            }

                                            return value.attchInstances.indexOf(instanceId) != -1;

                                        }).forEach(function(volume) {
                                            volumeList.push(volume);
                                        });
                                    }

                                    ctrl.filteredVolumesByAccount = volumeList.filter(function(item, index, array){ return array.indexOf(item) === index; });

                                    ctrl.showData = true;

                        });
                });
            });




        };

        ctrl.numberOfPages = function(length)  {
            return Math.ceil(length/ ctrl.pageSize);
        };

        ctrl.toggleView = function (index) {
            ctrl.toggledView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        ctrl.load();
    }
})();