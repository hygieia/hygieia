/**
 * Authorization interceptor for adding token to outgoing requests, and handling error responses
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('authInterceptor', authInterceptor);

    authInterceptor.$inject = ['$q', '$location', 'tokenService', 'loginRedirectUrl'];
    function authInterceptor($q, $location, tokenService, loginRedirectUrl) {
      var saveCurrentUrl = function () {
        if($location.path().toLowerCase() != '/login') {
          loginRedirectUrl.url = $location.path();
        }
        else {
          loginRedirectUrl.url = '/';
        }
      };

      return {
        responseError: function (response) {
          if (response.status === 401) {
            saveCurrentUrl();
            $location.path('/login');
          }
          return $q.reject(response);
        }
      };
    }
})();
