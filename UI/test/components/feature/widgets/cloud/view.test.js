/**
 * Created by hyw912 on 4/12/16.
 */

/**
 * Created by hyw912 on 4/11/16.
 */

describe('CloudWidgetViewController', function () {

    var controller;
    var scope;
    var cloudData;
    var ec2Data = {"instanceId":"id-1234","instanceType":"m3-large","imageId":"img-1234","imageExpirationDate":0,"imageApproved":false,"instanceOwner":"owner-1234","isMonitored":false,"privateDns":"whatever","privateIp":"1.1.1.1","publicDns":"whatever","publicIp":"1.1.1.1","subnetId":"sn-1234","virtualNetworkId":"vpc-1234","age":10,"isEncrypted":false,"status":"running","isStopped":false,"isTagged":false,"cpuUtilization":0.0,"lastUpdatedDate":"Apr 16, 2016 4:14:37 PM","securityGroups":["sg-01"],"tags":[{"name":"tag1","value":"value1"},{"name":"tag2","value":"value2"}],"networkIn":0.0,"networkOut":0.0,"diskRead":0.0,"diskWrite":0.0,"rootDeviceName":"Any/Device","lastAction":"stop"};



    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));

    beforeEach(module(function($provide) {
        $provide.factory('cloudData', function() {

            return {
                getEC2Data: getEC2Data
            };

            function getEC2Data() {
                return ec2Data;
            };

        })}));


    // inject the required services and instantiate the controller
    beforeEach(
        function() {
            inject(function ($rootScope, cloudData, $controller) {
                scope = $rootScope.$new();

                scope.widgetConfig = {
                    options: {
                        tag: "MyTag"
                    }
                };

                controller = $controller('CloudWidgetViewController', {
                    $scope: scope,
                    cloudData: cloudData
                });
            })});


    describe('load()', function() {
        describe('When I call load', function () {
            it('Then I expect AMI data to be retrieved', function() {

                //Arrange

                //Act
                var data = controller.load();

                //Assert
                var result = angular.equals( data,ec2Data );
                expect(result).toBeTruthy();
            });
        });
    });
});
