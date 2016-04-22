/**
 * Created by nmande on 4/12/16.
 */


describe('CloudWidgetViewController', function () {

    var controller;
    var scope;
    var cloudData;
    var instanceData = {

    };

    var ec2DataSummarizedByTag = {
        "ageAlert": 0,
        "ageError": 0,
        "ageGood": 0,
        "cpuAlert": 0,
        "cpuHigh": 0,
        "cpuLow": 0,
        "currency": "string",
        "diskAlert": 0,
        "diskHigh": 0,
        "diskLow": 0,
        "estimatedCharge": 0,
        "expiredImageCount": 0,
        "lastUpdated": 0,
        "memoryAlert": 0,
        "memoryHigh": 0,
        "memoryLow": 0,
        "networkAlert": 0,
        "networkHigh": 0,
        "networkLow": 0,
        "nonEncryptedCount": 0,
        "nonTaggedCount": 0,
        "stoppedCount": 0,
        "totalInstanceCount": 0
    };


    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));

    beforeEach(module(function($provide) {
        $provide.factory('cloudData', function() {

            return {
                getEC2DataSummarizedByTag: getEC2DataSummarizedByTag
            };

            function getEC2DataSummarizedByTag() {
                return ec2DataSummarizedByTag;
            }
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
                var result = angular.equals( data,ec2DataSummarizedByTag );
                expect(result).toBeTruthy();
            });
        });
    });
});
