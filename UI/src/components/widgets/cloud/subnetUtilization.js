/**
 * Detail controller for the build widget
 */
(function () {

    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SubnetUtilizationController', SubnetUtilizationController);

    SubnetUtilizationController.$inject = ['$scope', '$modalInstance', 'vpc', '$modal'];
    function SubnetUtilizationController($scope, $modalInstance, vpc, $modal) {
        var ctrl = this;
        ctrl.vpc = vpc;
        ctrl.close = close;

        function close() {
            $modalInstance.dismiss('close');
        }

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };
        
        ctrl.percentUsed = function(subnet) { 
            return subnet.usedIPCount/(subnet.usedIPCount + subnet.availableIPCount) * 100;
        }

        ctrl.utilizationPercent = function(subnet) {
            var util = ctrl.percentUsed(subnet);
            return {series: [(100 - util), util]};
        };        
        
        ctrl.aggregateSubnetsByAz = function(subnets) {
          var azMap = {};
          var availabilityZones = []
          angular.forEach(subnets, function(subnet) {

            if (!azMap[subnet.zone]) {
              azMap[subnet.zone] = [];
              var az = {};
              az.name = subnet.zone;
              az.subnets = azMap[subnet.zone]
              availabilityZones.push(az);
            };
    
            azMap[subnet.zone].push(subnet);
          });
          return availabilityZones;
        };

        ctrl.subnets =  ctrl.aggregateSubnetsByAz(ctrl.vpc.subnets);        

        ctrl.detail = function(subnet) {
          $modal.open({
                controller: 'SubnetDetailController',
                controllerAs: 'subnetDetail',                
                templateUrl: 'components/widgets/cloud/subnetDetail.html',
                size: 'lg',
                resolve: {
                  subnet: function() {
                    return subnet;
                  }
                }

            });          
        }
 
    }
})();
