/**
 * Service to handle all authorization operations
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .service('authService', authService);

    authService.$inject = ['signupData', 'loginData', 'tokenService'];
    function authService(signupData, loginData, tokenService) {

        var processResponse = function (response) {
          tokenService.setToken(response.headers()['x-authentication-token']);
          return response;
        }

        this.register = function (credentials) {
          return signupData.signup(credentials.username, credentials.password).then(processResponse)
        }

        this.login = function (credentials) {
          return loginData.login(credentials.username, credentials.password).then(processResponse)
        }

        this.loginLdap = function (credentials) {
          return loginData.loginLdap(credentials.username, credentials.password).then(processResponse)
        }

        this.logout = function () {
          tokenService.removeToken();
        }
    }
})();
