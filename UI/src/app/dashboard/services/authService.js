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

        this.register = function (credentials) {
          return signupData.signup(credentials.username, credentials.password).then(function (response) {
            tokenService.setToken(response.headers()['x-authentication-token']);
            return response;
          })
        }

        this.login = function (credentials) {
          return loginData.login(credentials.username, credentials.password).then(function (response) {
            tokenService.setToken(response.headers()['x-authentication-token']);
            return response;
          })
        }

        this.logout = function () {
          tokenService.removeToken();
        }
    }
})();
