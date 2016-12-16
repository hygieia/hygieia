angular.module(HygieiaConfig.module).service('userService', function ($window, jwtHelper) {

  var getUser = function () {
    var token = $window.localStorage.token;
    if(token) {
      return jwtHelper.decodeToken(token);
    }
    return {};
  }

  this.getUsername = function () {
    return getUser().sub;
  }

});
