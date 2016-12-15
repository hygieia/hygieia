angular.module(HygieiaConfig.module).factory('authInterceptor', function ($rootScope, $q, $window, $location) {
  return {
    request: function (config) {
      config.headers = config.headers || {};
      if ($window.localStorage.token) {
        config.headers.Authorization = 'Bearer ' + $window.localStorage.token;
      }
      return config;
    },
    responseError: function (response) {
      if (response.status === 401) {
        $location.path('/');
      }

      return $q.reject(response);
    }
  };
});
