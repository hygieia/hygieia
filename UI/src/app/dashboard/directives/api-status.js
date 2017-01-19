(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('apiStatus', apiStatus);

    function apiStatus() {
        return {
            restrict: 'E',
            templateUrl: 'app/dashboard/views/api-status.html',
            controller: ['$scope', '$http', function ApiStatusController($scope, $http) {
              function getAppVersion(){
                  var url = '/api/appinfo';
                  $http.get(url, {skipAuthorization: true}).success(function (data, status) {
                      console.log("appinfo:"+data);
                      $scope.appVersion=data;
                      $scope.apiup = (status == 200);
                  }).error(function(data,status){
                      console.log("appInfo:"+data);
                      $scope.appVersion="0.0";
                      $scope.apiup = false;
                  });
              }
              getAppVersion();
            }]
        };
    }
})();
