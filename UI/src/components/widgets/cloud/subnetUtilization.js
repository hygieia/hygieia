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
        $scope.viewToDisplay = "IPUtilization";
        function close() {
            $modalInstance.dismiss('close');
        };

        ctrl.pieOptions = {
            donut: true,
            donutWidth: 20,
            startAngle: 270,
            total: 200,
            showLabel: false
        };



        ctrl.showOverSubscription = function() {
          $scope.viewToDisplay = "OverSubscription"
          $scope.subnets =  ctrl.subnetsForOveSub;        
        };

        ctrl.showIpUtilization = function() {
           $scope.viewToDisplay = "IPUtilization";
           $scope.subnets =  ctrl.subnetsForIpUtil;       
        };      

        $scope.dispayView = function(view) {
          return ($scope.viewToDisplay === view);
        }
        
        ctrl.percentUsed = function(subnet) { 
            return subnet.usedIPCount/(subnet.usedIPCount + subnet.availableIPCount) * 100;
        }

        ctrl.percentSubscribed = function(subnet) {
          var subscribed = subnet.subscribedIPCount;
          var available = subnet.availableIPCount;
          return subscribed/(subscribed + available) * 100;
        }

        ctrl.subnetDetailData = function(subnet) {
          if ($scope.viewToDisplay === "IPUtilization") {
            var util = ctrl.percentUsed(subnet);
            return {series: [ {meta: 'used', value: util}, {meta:'available', value: (100 - util)}]};
          }
          else
          {
            var util = ctrl.percentSubscribed(subnet);
            return {series: [ {meta: 'used', value: util}, {meta:'available', value: (100 - util)}]};            
          }
        };   

        $scope.utillizationEvents = {

          draw: function (data) {

            if (data.type === 'slice') {

              var strokeColor = '#05ac45';

              if (data.meta == 'used') {
                  strokeColor = '#d8514d';
              }
              
              data.element.attr(
              {
                style: 'stroke-width: 20px; stroke: ' + strokeColor + ';'
              });
            }
          }
        }
        
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

        ctrl.subnetsForIpUtil =  ctrl.aggregateSubnetsByAz(ctrl.vpc.subnets);   
        ctrl.subnetsForOveSub =  ctrl.aggregateSubnetsByAz(ctrl.vpc.subnets);   

        $scope.subnets = ctrl.subnetsForIpUtil;

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
