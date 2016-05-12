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

        ctrl.getAverageInstanceCountPerDay = function(instances)
        {

            var masterArray=[];
            var obj = JSON.parse(JSON.stringify(instances));


            //Get Unique Dates as Keys

            var uniqueDates=[];
            for(var l=0;l<obj.length;l++) {

                var myDay = convertEpochTimeToDate(obj[l].time);
                if (uniqueDates.indexOf(myDay) == -1) {
                    uniqueDates.push(myDay)
                }
            }


            // I have array of Unique Dates
           for(var i=0;i<uniqueDates.length;i++)
            {

                var date = uniqueDates[i];
                var oneDay = instances.filter(function(value) {
                        return convertEpochTimeToDate(value.time) == date;
                });


                var total = oneDay.reduce(function(sum, currentValue) {
                    return sum + currentValue.total;
                }, 0);

                var cnt = oneDay.length;

                masterArray.push({
                    date:date,
                    avg: (total/cnt)
                })

            }

           return masterArray;

        }

        ctrl.load = function () {



            cloudHistoryData.getInstanceHistoryDataByAccount(ctrl.accountNumber)
                .then(function (instanceDataHistory) {

                    var masterArray=ctrl.getAverageInstanceCountPerDay(instanceDataHistory);




                    console.log("MasterArray:"+JSON.stringify(masterArray));


                    ctrl.instanceDataHistory = instanceDataHistory;

                    var obj = JSON.parse(JSON.stringify(instanceDataHistory));
                    var timeSeries = [];
                    var totals = [];
                    for (var k = 0; k < obj.length; k++) {
                        var m = {
                            meta: convertEpochTimeToDate(obj[k].time) + "  " + obj[k].total,
                            value: obj[k].total
                        };

                        timeSeries.push(m);

                    }

                    ctrl.instanceHistorySeries = {series : [timeSeries]};

                    ctrl.lineOptions = {
                        plugins: [
                            Chartist.plugins.gridBoundaries(),
                            Chartist.plugins.lineAboveArea(),
                            Chartist.plugins.tooltip(),
                            Chartist.plugins.pointHalo(),
                            Chartist.plugins.axisLabels({
                                axisX: {
                                    type: Chartist.AutoScaleAxis
                                }
                            })

                        ],
                        showArea: false,
                        lineSmooth: true,
                        fullWidth: true,
                        width: 400,
                        height: 300,
                        chartPadding: 7,
                        axisY: {
                            offset: 30,
                            showGrid: true,
                            showLabel: true,
                            labelInterpolationFnc: function(value) { return Math.round(value * 100) / 100; }
                        }

                    };

                });

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

                            var subnet = ctrl.subnetsByAccount.find(function(value) {
                                return value.subnetId == element.subnetId
                            });

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