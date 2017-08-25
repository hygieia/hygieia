/**
 * Standard date picker
 *
 * Example for use:
 *  1) Disable every date before today's date from selection
 *  <date-picker disable-before-today="true" dp-Name="value_of_name_attribute"></date-picker>
 *
 * 2) Disable every date after today's date from selection
 * <date-picker disable-after-today="true" dp-Name="value_of_name_attribute"></date-picker>
 *
 * 3) No disabled dates
 * <date-picker dp-Name="value_of_name_attribute"></date-picker>
 *
 * 4) For passing default date value must be of type Date
 * <date-picker dp-Name="value_of_name_attribute" ng-model="ctrl.date"></date-picker>
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('datePicker', function() {
            return {
                restrict: 'E',
                scope: {
                    model: "=",
                    date: '=ngModel',
                    dpName: "@",
                    disableBeforeToday: "@",
                    disableAfterToday: "@"
                },
                templateUrl: 'app/dashboard/views/datePicker.html',

                link: function(scope) {

                    scope.today = function() {
                        var date = scope.date;
                        if(date instanceof Date){
                            scope.dt = date
                        }else{
                            scope.dt = new Date();
                        }
                    };
                    scope.today();

                    scope.clear = function () {
                        scope.dt = null;
                    };

                    scope.toggleMin = function() {
                        scope.minDate = scope.minDate ? null : new Date();
                    };
                    scope.toggleMin();

                    scope.open = function() {
                        scope.status.opened = true;
                    };

                    scope.setDate = function(year, month, day) {
                        scope.dt = new Date(year, month, day);
                    };

                    scope.dateOptions = {
                        minDate: scope.minDate,
                        dateDisabled: disabled,
                        formatYear: 'yy',
                        startingDay: 1
                    };

                    scope.formats = ['MM/dd/yyyy', 'dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
                    scope.format = scope.formats[0];

                    scope.status = {
                        opened: false
                    };

                    var tomorrow = new Date();
                    tomorrow.setDate(tomorrow.getDate() + 1);
                    var afterTomorrow = new Date();
                    afterTomorrow.setDate(tomorrow.getDate() + 2);
                    scope.events =
                        [
                            {
                                date: tomorrow,
                                status: 'full'
                            },
                            {
                                date: afterTomorrow,
                                status: 'partially'
                            }
                        ];

                    scope.getDayClass = function(date, mode) {
                        if (mode === 'day') {
                            var dayToCheck = new Date(date).setHours(0,0,0,0);

                            for (var i=0;i<scope.events.length;i++){
                                var currentDay = new Date(scope.events[i].date).setHours(0,0,0,0);

                                if (dayToCheck === currentDay) {
                                    return scope.events[i].status;
                                }
                            }
                        }

                        return '';
                    };
                    function disabled(data){
                        var date = data.date,
                            mode = data.mode;
                        var disableFlag = false;
                        var yesterday = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);

                        if(scope.disableBeforeToday){
                            disableFlag = date < yesterday;
                        }else if(scope.disableAfterToday){
                            disableFlag = date > yesterday;
                        }

                        return ( mode === 'day' && ( disableFlag ) );
                    }
                }
            };
        });

})();