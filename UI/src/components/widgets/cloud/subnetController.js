(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('subnetController', SubnetController);

    SubnetController.$inject = ['$scope', 'cloudData', '$http', '$modal', '$compile'];
    
    function SubnetController($scope, cloudData, $http, $modal, $compile) {

        var ctrl = this;
        $scope.vpcs = [];

        ctrl.accountNumber = $scope.widgetConfig.options.accountNumber || "";



        $scope.ipUtilizations;


       $scope.ipUtilizationsOptions = {
            horizontalBars: true,
            stackBars: true,
            
            axisY: {
    offset: 100
  },
  axisX: {
    offset:20,
     labelInterpolationFnc: function(value, index) {
      return index % 2 === 0 ? value : null;
    }
  }


        };

        $scope.ipUtilizationsEvents = { 

  draw: function (data) {

if (data.type === 'bar') {
  var strokeColor = '##05ac45';

  if (data.meta == 'high') {
        strokeColor = '#d8514d'
  } else if (data.meta == 'med') {
      strokeColor = '#ffbd35'
  }
    data.element.attr({
      style: 'stroke-width: 20px; stroke: ' + strokeColor + ';'
    });
  }

  if (data.type === 'label') {
    //var compiledHtml = $compile(strElm);
    //element.append(compiledHtml);
    data.valueOf;
    data.element.attr({
     // onclick: "angular.element(document.getElementById('iptutildiv')).scope().viewSubnetUtilization2(this.innerHTML)",
      class: 'clickable'
    });  }

  }

      }

        ctrl.calculatePercentage = function(count, total) {
          var percentage = count / total * 100;
          return Math.round(percentage);
        }

        $scope.totalAvailable = 0;
        $scope.totalUsed = 0;

        $scope.getIpUtilizations = function() {
            $scope.getSubnets
            var vpcData = {labels:[], series:[[], [], []]};
            angular.forEach($scope.vpcs, function(vpc) {
              
              var angularModalPopUpCall = "angular.element(document.getElementById('iptutildiv')).scope().viewSubnetUtilization('" + vpc.id + "')";

              var clickableLabel = '<div class="clickable" onclick="' + angularModalPopUpCall +'">' + vpc.id + '</div>';

              vpcData.labels.push(clickableLabel);
              var countOfSubnetsByUtilization = vpc.countOfSubnetsByUtilization;
              var totalSubnets = countOfSubnetsByUtilization.high + countOfSubnetsByUtilization.med + countOfSubnetsByUtilization.low;

              vpcData.series[0].push({ meta: 'low', value:ctrl.calculatePercentage(countOfSubnetsByUtilization.low, totalSubnets)});
              vpcData.series[1].push({ meta: 'med', value:ctrl.calculatePercentage(countOfSubnetsByUtilization.med, totalSubnets)});
              vpcData.series[2].push({ meta: 'high', value:ctrl.calculatePercentage(countOfSubnetsByUtilization.high, totalSubnets)});

              $scope.totalAvailable += vpc.ips.totalAvailable;
              $scope.totalUsed += vpc.ips.totalUsed ;

            });

            $scope.ipUtilizations = vpcData;
            $scope.totalIPs = $scope.totalAvailable + $scope.totalUsed;
            $scope.avgUtilization = ctrl.calculatePercentage($scope.totalUsed, $scope.totalIPs);

        }

        $scope.getSubnets = function() {

            cloudData.getAWSSubnetsByAccount(ctrl.accountNumber)
                .then(function(subnets){
                    $scope.vpcs = ctrl.groupByVpc(subnets);
                    $scope.getIpUtilizations();
                    console.log($scope.vpcs)
            });
        };

$scope.getHeight = function() {

    return $scope.vpcs.length * 30;
}
        $scope.viewSubnetUtilization = function(vpcId) {
            
            
            var vpc = {};

            angular.forEach($scope.vpcs, function(item) {
              if (item.id.toUpperCase() == vpcId.toUpperCase()) {
                  vpc = item;
              }
            });

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
              vpc.ips = {totalAvailable:0, totalUsed: 0};
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

            vpcMap[subnet.virtualNetworkId].ips.totalAvailable += subnet.availableIPCount;
            vpcMap[subnet.virtualNetworkId].ips.totalUsed += subnet.usedIPCount;

          });
          return vpcs;
        };

    }

})();