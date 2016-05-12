(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('subnetController', SubnetController);

    SubnetController.$inject = ['$scope', 'cloudData', '$http', '$modal'];

    function SubnetController($scope, cloudData, $http, $modal) {

        var ctrl = this;
        $scope.vpcs = [];

        ctrl.accountNumber = $scope.widgetConfig.options.accountNumber || "";

        $scope.getSubnets = function() {
            cloudData.getAWSSubnetsByAccount(ctrl.accountNumber)
                .then(function(subnets){
                    $scope.vpcs = ctrl.groupByVpc(subnets);
            });
        };

        $scope.viewSubnetUtilization = function(vpc) {
            $modal.open({
                controller: 'SubnetUtilizationController',
                controllerAs: 'subnetUtilization',                
                templateUrl: 'components/widgets/cloud/subnetUtilization.html',
                size: 'lg',
                resolve: {
                  vpc: function() {
                    return vpc;
                  }
                }

            });
        };

        ctrl.calculateUtilization = function(subnet) {
            return subnet.usedIPCount/(subnet.usedIPCount + subnet.availableIPCount) * 100;
        }

        ctrl.groupByVpc = function(subnets) {
          var vpcMap = {};
          var vpcs = []
          angular.forEach(subnets, function(subnet) {
            if (!vpcMap[subnet.virtualNetworkId]) {
              var vpc = {};
              vpc.id = subnet.virtualNetworkId;
              vpc.subnets = [];
              vpc.countOfSubnetsByUtilization = {};
              vpc.countOfSubnetsByUtilization.high = 0;
              vpc.countOfSubnetsByUtilization.med = 0;
              vpc.countOfSubnetsByUtilization.low = 0;
              vpcs.push(vpc);
              vpcMap[subnet.virtualNetworkId] = vpc;              
            };
            vpcMap[subnet.virtualNetworkId].subnets.push(subnet);
            var utilization = ctrl.calculateUtilization(subnet);
            if (utilization > 70) {
              vpcMap[subnet.virtualNetworkId].countOfSubnetsByUtilization.high+=1;
            } else if (utilization <= 70 && utilization > 50) {
                vpcMap[subnet.virtualNetworkId].countOfSubnetsByUtilization.med+=1;
            } else {
                vpcMap[subnet.virtualNetworkId].countOfSubnetsByUtilization.low+=1;
            }
          });
          return vpcs;
        };

    }

})();