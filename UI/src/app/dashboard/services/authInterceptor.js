/**
 * Authorization interceptor for adding token to outgoing requests, and handling error responses
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('authInterceptor', authInterceptor);

    authInterceptor.$inject = ['$q', '$location', 'tokenService'];
    function authInterceptor($q, $location, tokenService) {
        return {
            // adds authorization token to outgoing requests
            request: function(config) {
                config.headers['Authorization'] = tokenService.getToken();
                return config;
            },

            // handles error responses
            responseError: function (response) {
                if (response.status === 401 || response.status === 403) {
                    tokenService.removeToken();
                    $location.path('/login');
                }
                return $q.reject(response);
            }
        };
    }
})();
