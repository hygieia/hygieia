/**
 * Session Factory - This service updates the session variables
 * for the user by making a call to our API to create the user details of which it got from
 * SSO.
 *
 * @return {[type]} [description]
 */
(function() {
  'use strict';

  angular
    .module(HygieiaConfig.module + '.core')
      .factory('Session', Session);

    Session.$inject = ['$http', '$window', 'userService', '$cookies'];
    function Session($http, $window, userService, $cookies) {
    	return {
    		updateSession : updateSession
    	}
    	
    	function updateSession() {
			if(!userService.isAuthenticated()) {
				var requestCookies = $cookies.getAll();
				if(angular.isUndefined($cookies.get('HTTP_USERC'))) {
					return 'sso not enabled';
				}
				var req = {
						 method: 'GET',
						 url: '/api/findUser',
						 headers: {
							 'cookiesheader': angular.toJson(requestCookies)
						 }
				}

				return $http(req).then(function scss(response) {
		        	$window.localStorage.token = response.headers()['x-authentication-token'];
		            return true;
		        }, function err(error) {
		        	return "empty";
		        });
			} else {
				return "authenticated";
			}
		}
    }
})();