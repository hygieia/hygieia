/**
 * Created by nmande on 4/12/16.
 */


describe('CloudWidgetViewController', function () {

    var controller;
    var scope;
    var cloudData;
    var AWSGlobalData = {
        "compute": {
            "ec2Instances": 3015,
            "running": 1900,
            "stopped": 300,
            "excluded": 910
        },
        "s3": {
            "s3Buckets": 9000,
            "encrypted": 35,
            "tagged": 45,
            "compliant": 54
        }
    };

    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));

    beforeEach(module(function($provide) {
        $provide.factory('cloudData', function() {

            return {
                getAWSGlobalData: getAWSGlobalData
            };

            function getAWSGlobalData() {
                return AWSGlobalData;
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
            it('Then I expect AMI data to be retrieved into awsOverview', function() {

                //Act-Arrange

                //Assert
                var result = angular.equals( controller.awsOverview,AWSGlobalData );
                expect(result).toBeTruthy();
            });
        });
    });

    describe('toggleView()', function() {
        describe('When I call toggleView and isDetail is false', function () {
            it('Then I expect isDetail to change to true', function() {

                //Arrange
                controller.isDetail = false;

                //Act
                controller.toggleView();

                //Assert

                expect(controller.isDetail).toBeTruthy();
            });
        });

        describe('When I call toggleView and isDetail is true', function () {
            it('Then I expect isDetail to change to false', function() {

                //Arrange
                controller.isDetail = true;

                //Act
                controller.toggleView();

                //Assert

                expect(controller.isDetail).toBeFalsy();
            });
        });
    });
});
