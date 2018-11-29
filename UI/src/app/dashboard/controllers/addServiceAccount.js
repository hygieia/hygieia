(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AddServiceAccountController', AddServiceAccountController);

    AddServiceAccountController.$inject = ['$uibModalInstance', 'serviceAccountData', '$scope'];

    function AddServiceAccountController($uibModalInstance, serviceAccountData, $scope) {
        var ctrl = this;
        // public methods
        ctrl.submit = submit;

        function submit(form) {
            if (form.$valid) {
                console.log('val is ' + document.cdf.serviceAccount.value);
                console.log('val is ' + document.cdf.fileNames.value);
                var account = {
                    "serviceAccount": document.cdf.serviceAccount.value,
                    "fileNames": document.cdf.fileNames.value
                };
                serviceAccountData
                    .createAccount(account)
                    .success(function (response) {
                        $uibModalInstance.close();
                    })
                    .error(function (response) {
                        console.log(response);

                    });
            }
        }
    }
})();
