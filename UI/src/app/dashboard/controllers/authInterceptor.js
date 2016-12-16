angular.module(HygieiaConfig.module).factory('authInterceptor', function ($q, $location, tokenService) {
  return {
    request: function (config) {
      config.headers = config.headers || {};
      var token = tokenService.getToken();
      if (token) {
        config.headers.Authorization = 'Bearer ' + token;
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
