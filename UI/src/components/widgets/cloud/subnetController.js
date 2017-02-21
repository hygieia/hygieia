(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('subnetController', SubnetController);

    SubnetController.$inject = ['$scope', 'cloudData', '$http', '$uibModal'];
    
    function SubnetController($scope, cloudData, $http, $uibModal) {

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
               // return index % 2 === 0 ? value : null;
               return '';
              }
            }
        };

        $scope.ipUtilizationsEvents = {

          draw: function (data) {

            if (data.type === 'bar') {
              var strokeColor = '#05ac45';

              if (data.meta == 'high') {
                strokeColor = '#d8514d'
              } else if (data.meta == 'med') {
                strokeColor = '#ffbd35'
              }


              data.element.attr(
              {
                style: 'cursor: pointer; stroke-width: 20px; stroke: ' + strokeColor + ';',
                onclick: "angular.element(document.getElementById('iptutildiv')).scope().viewSubnetUtilization('" + data.series[data.index].vpc + "')"
              });

              if (data.value.x > 0) {
              var label, labelText, barLength, labelWidth, barClasses,
                          barWidth = 20,
                          barHorizontalCenter = (data.x1 + (data.element.width() * .5)),
                          barVerticalCenter =  (data.y1 + (barWidth * .12));


                      // add the custom label text as an attribute to the bar for use by a tooltip
                      data.element.attr({ label: labelText }, "ct:series");

                      label = new Chartist.Svg("text");



                      label.text(data.series[data.index].count);
                      label.attr({
                          x: barHorizontalCenter,
                          y: barVerticalCenter,
                          "text-anchor": "middle",
                          style: "cursor: pointer;font-family: 'proxima-nova-alt', Helvetica, Arial, sans-serif; font-size: 12px; fill: white",
                          onclick: "angular.element(document.getElementById('iptutildiv')).scope().viewSubnetUtilization('" + data.series[data.index].vpc + "')"

                      });

                      // add the new custom text label to the bar
                      data.group.append(label);              
                          }
          }

              if (data.type === 'label') {
                data.valueOf;
                data.element.attr(
                {
                  class: 'clickable'
                }
              );  
            }
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

              vpcData.series[0].push({ meta: 'low', vpc: vpc.id, count: countOfSubnetsByUtilization.low,  value:ctrl.calculatePercentage(countOfSubnetsByUtilization.low, totalSubnets)});
              vpcData.series[1].push({ meta: 'med', vpc: vpc.id, count: countOfSubnetsByUtilization.med, value:ctrl.calculatePercentage(countOfSubnetsByUtilization.med, totalSubnets)});
              vpcData.series[2].push({ meta: 'high', vpc: vpc.id, count: countOfSubnetsByUtilization.high, value:ctrl.calculatePercentage(countOfSubnetsByUtilization.high, totalSubnets)});

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

            $uibModal.open({
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
