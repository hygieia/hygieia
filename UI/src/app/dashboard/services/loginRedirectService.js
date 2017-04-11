/**
 * Service to handle url redirects after login
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .service('loginRedirectService', loginRedirectService);

    loginRedirectService.$inject = ['signupData'];
    function loginRedirectService(signupData) {

        var path = '/';

        this.saveCurrentPath = function (currentUrl) {
          var hashIndex = currentUrl.indexOf('#');
          var oldRoute = currentUrl.substr(hashIndex + 1);
          path = oldRoute;
        }

        this.getRedirectPath = function () {
          var previousPath = path;
          if (previousPath != '/login') {
            return path;
          }

          return '/';
        }

    }
})();
