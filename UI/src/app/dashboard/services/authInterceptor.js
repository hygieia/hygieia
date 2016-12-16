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
        responseError: function (response) {
          if (response.status === 401) {
            $location.path('/');
          }
          if (response.status === 500) {
            $location.path('/');
          }

          return $q.reject(response);
        }
      };
    }
})();
