/**
 * Service to handle all user operations
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .service('userService', userService);

    userService.$inject = ['tokenService', 'jwtHelper'];
    function userService(tokenService, jwtHelper) {
      var getUser = function () {
        var token = tokenService.getToken();
        if (token) {
          return jwtHelper.decodeToken(token);
        }

        return {};
      }

      this.getUsername = function () {
        return getUser().sub;
      }

      this.getExpiration = function () {
        return getUser().expiration;
      }

      this.isAuthenticated = function () {
        if(this.getUsername() && !jwtHelper.isTokenExpired(tokenService.getToken())) {
          return true;
        }
        return false;
      }

      this.getAuthType = function () {
    	  return getUser().details;
      }

      this.isAdmin = function () {
        var user = getUser();
        if (user.roles && user.roles.indexOf("ROLE_ADMIN") > -1) return true;
        return false;
      }

      this.hasDashboardConfigPermission = function (owner, owners) {
    	if (this.isAdmin()) {
    		return true;
    	}

    	var authtype = this.getAuthType();
    	var username = this.getUsername();

    	// preexisting dashboards
      	if (authtype === 'STANDARD' && owner === username) {
      		return true;
      	}

      	var hasPermission = false;
      	owners.forEach(function (owner) {
      		if (owner.username === username && owner.authType === authtype) {
      			hasPermission = true;
      		}
      	});

      	return hasPermission;
      }
    }
})();
