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
