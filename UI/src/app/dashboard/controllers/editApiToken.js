(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('EditApiTokenController', EditApiTokenController);

    EditApiTokenController.$inject = ['$uibModalInstance','userData','tokenItem'];
    function EditApiTokenController($uibModalInstance, userData, tokenItem) {

        var ctrl = this;
        ctrl.apiUser = tokenItem.apiUser;
        ctrl.date =  new Date(tokenItem.expirationDt);

        // public methods
        ctrl.submit = submit;

        function submit(form) {

            form.expDt.$setValidity('apiTokenError', true);

            if (form.$valid) {
                console.log('val is ' + document.cdf.apiUser);
                console.log('val is ' + document.cdf.apiUser.value);
                console.log('dt is ' + document.cdf.expDt);
                console.log('dt is ' + document.cdf.expDt.value);
                var id = tokenItem.id
                var selectedDt = Date.parse(document.cdf.expDt.value);
                var momentSelectedDt = moment(selectedDt);
                var timemsendOfDay = momentSelectedDt.endOf('day').valueOf();

                var apitoken = {
                    "apiUser" : document.cdf.apiUser.value,
                    "expirationDt" : timemsendOfDay
                };

                userData
                    .updateToken(apitoken, id)
                    .success(function (response) {
                        console.log(response);
                        $uibModalInstance.close();
                    })
                    .error(function(response) {
                        console.log(response);
                        form.expDt.$setValidity('apiTokenError', false);
                    });
            }
        }
    }
})();