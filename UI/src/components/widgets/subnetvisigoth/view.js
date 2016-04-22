(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SubnetVisigothViewController', SubnetVisigothViewController);

    SubnetVisigothViewController.$inject = ['$q', '$scope', '$modal', '$http'];
    function SubnetVisigothViewController($q, $scope, $modal, $http) {
        var ctrl = this;

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };
        
        ctrl.utilizationPercent = function(util) {
            return {series: [(100 - util), util]};
        };
        
        $scope.getSubnetName = function(subnet) {
            var subnetName = ""
            angular.forEach(subnet.tags, function(tag) {
            if (tag.key == "Name") {
              subnetName = tag.value;
            };
            });

          return subnetName;
          };

        ctrl.aggregateSubnetsByAz = function(subnets) {
          var azMap = {};
          var availabilityZones = []
          angular.forEach(subnets, function(subnet) {

            if (!azMap[subnet.subnet.availabilityZone]) {
              azMap[subnet.subnet.availabilityZone] = [];
              var az = {};
              az.name = subnet.subnet.availabilityZone;
              az.subnets = azMap[subnet.subnet.availabilityZone]
              availabilityZones.push(az);
            };
    
            azMap[subnet.subnet.availabilityZone].push(subnet);
          });
          return availabilityZones;
        };

      $scope.subnets = [];
      $scope.getSubnets = function() {
        console.log('in getsubnets');
        var result = {};
        var requestBody = '{"percentUsedIps":"0","eventType":"API","subnetFilter":[{"name":"vpc-id","values":["THEVPCIDGOESHERE"]},{"name":"state","values":["available"]} ]}'; 
        
        result = $http.post('https://SUBNETVISIGOTHAPIENDPOINTGOESHERE',  requestBody)
                    .success(function(data, status, headers, config) {
                      // this callback will be called asynchronously
                      // when the response is available
                       $scope.subnets = ctrl.aggregateSubnetsByAz(data);})
                    .error(function(data, status, headers, config) {
                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                      console.log(status)
                    });
        return result;
      }

        ctrl.load = function() {
        };


    }
})();
