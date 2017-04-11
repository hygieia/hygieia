(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('loginForm', loginForm);

    function loginForm() {
        return {
            restrict: 'E',
            scope: {
              authType: '='
            },
            templateUrl: 'app/dashboard/views/login-form.html',
            controller: ['$scope', '$location', 'loginRedirectService', function loginFormController($scope, $location, loginRedirectService) {

              $scope.login = function() {
                $scope.lg.username.$setValidity('invalidUsernamePassword', true);
                var valid = $scope.lg.$valid;
                if (valid) {
                    var auth = {'username': $scope.lg.username.$modelValue, 'password': $scope.lg.password.$modelValue};
                    $scope.authType.login(auth)
                        .then(function (response) {
                            if (response.status == 200) {
                                $location.path(loginRedirectService.getRedirectPath());
                            } else if (response.status == 401) {
                                $scope.lg.username.$setValidity(
                                        'invalidUsernamePassword',
                                        false
                                      );
                            }
                        });
                }
              }
            }]
        };
    }
})();
