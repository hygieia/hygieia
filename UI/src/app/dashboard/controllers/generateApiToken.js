(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('GenerateApiTokenController', GenerateApiTokenController);

    GenerateApiTokenController.$inject = ['$uibModalInstance', 'userService', 'userData', '$scope'];
    function GenerateApiTokenController($uibModalInstance, userService, userData, $scope) {

        var ctrl = this;

        // public methods
        ctrl.submit = submit;

        function processUserResponse(response) {
            $scope.users = response;
        }

        //////////
        $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.clear = function () {
            $scope.dt = null;
        };

        // Disable weekend selection
        $scope.disabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
        };

        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };
        $scope.toggleMin();

        $scope.open = function() {
            $scope.status.opened = true;
        };

        $scope.setDate = function(year, month, day) {
            $scope.dt = new Date(year, month, day);
        };

        $scope.dateOptions = {
            minDate: $scope.today(),
            dateDisabled: function (data) {
                var date = data.date,
                    mode = data.mode;

                var yesterday = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);

                return ( mode === 'day' && ( date < yesterday ) );
            },
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.formats = ['MM/dd/yyyy', 'dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];

        $scope.status = {
            opened: false
        };

        var tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        var afterTomorrow = new Date();
        afterTomorrow.setDate(tomorrow.getDate() + 2);
        $scope.events =
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

        $scope.getDayClass = function(date, mode) {
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0,0,0,0);

                for (var i=0;i<$scope.events.length;i++){
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }

            return '';
        };
        ////////

        function submit(form) {

            form.apiKey.$setValidity('apiTokenError', true);

            if (form.$valid) {
                console.log('val is ' + document.cdf.apiUser);
                console.log('val is ' + document.cdf.apiUser.value);
                console.log('dt is ' + document.cdf.expDt);
                console.log('dt is ' + document.cdf.expDt.value);

                var selectedDt = Date.parse(document.cdf.expDt.value);
                var momentSelectedDt = moment(selectedDt);
                var timemsendOfDay = momentSelectedDt.endOf('day').valueOf();

                var apitoken = {
                    "apiUser" : document.cdf.apiUser.value,
                    "expirationDt" : timemsendOfDay
                };

                userData
                    .createToken(apitoken)
                    .success(function (response) {
                        console.log(response);
                        //$scope.apiKey = response;
                        ctrl.apiKey = response;
                        //$uibModalInstance.close();
                    })
                    .error(function(response) {
                        console.log(response);
                        ctrl.apiKey = response;
                        form.apiKey.$setValidity('apiTokenError', false);
                    });
            }
            else
            {
                //form.apiToken.$setValidity('apiTokenError', false);
            }

        }

    }
})();
