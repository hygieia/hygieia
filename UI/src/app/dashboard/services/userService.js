angular.module(HygieiaConfig.module).service('userService', function (tokenService, jwtHelper) {

  var getUser = function () {
    var token = tokenService.getToken();
    if(token) {
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

  this.isAdmin = function () {
    //TODO: check role
    if(this.getUsername() === 'admin') {
      return true;
    }
    return false;
  }

});
