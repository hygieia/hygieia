(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('featureFlagsData', featureFlagsData);

    function featureFlagsData($http) {
        var testFeatureFlagsRoute = 'test-data/feature_flags.json';
        var adminRoute = '/api/admin';

        return {
            getFeatureFlagsData: getFeatureFlagsData,
            createOrUpdateFeatureFlags: createOrUpdateFeatureFlags,
            deleteFeatureFlags : deleteFeatureFlags
         };

      function getFeatureFlagsData(){
        var route = adminRoute + "/featureFlags";
        return $http.get(HygieiaConfig.local ? testFeatureFlagsRoute : route);
      
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
