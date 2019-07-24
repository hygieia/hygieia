(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('featureFlagsData', featureFlagsData);

    function featureFlagsData($http) {
        var testDetailRoute = 'test-data/signup_detail.json';
        var adminRoute = '/api/admin';

        return {
            getFeatureFlagsData: getFeatureFlagsData,
            createOrUpdateFeatureFlags: createOrUpdateFeatureFlags,
            deleteFeatureFlags : deleteFeatureFlags
         };


        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
              console.log("Data="+ JSON.stringify(response.data));
                return response.data;
            });
        }

      function getFeatureFlagsData(){
          var route = adminRoute + "/featureFlags";
          if(HygieiaConfig.local)
          {
            console.log("In local testing");
            return getPromise(testDetailRoute);
          }
          else
          {
        return $http.get(route);
      }
    }


        function createOrUpdateFeatureFlags(flags) {
            var route = adminRoute + "/addOrUpdateFeatureFlags";
            return $http.post(route, flags);
        }


        function deleteFeatureFlags(id){
            var route = adminRoute + "/deleteFeatureFlags";
            return $http.delete(route+"/"+id).then(function (response) {
                return response.data;
            });
        }

  }
})();
