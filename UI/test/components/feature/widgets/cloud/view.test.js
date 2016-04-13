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
    var testData;

    beforeEach(function() {
        testData = [{
            "ec2InstanceId": "i-8572b106",
            "amiId": "ami-6eb7ee04",
            "amiEndOfLifeDate": "2016-03-01",
            "ec2InstanceUsingApprovedAmi": true,
            "ec2InstanceUsingExpiredAmi": true,
            "ec2InstanceUsingAmiExpiringInTwoWeeks": false,
            "ec2InstanceOwnerId": "mhi299"
        }, {
            "ec2InstanceId": "i-e92f2d5a",
            "amiId": "ami-f7cc809d",
            "amiEndOfLifeDate": "2016-02-01",
            "ec2InstanceUsingApprovedAmi": true,
            "ec2InstanceUsingExpiredAmi": true,
            "ec2InstanceUsingAmiExpiringInTwoWeeks": false,
            "ec2InstanceOwnerId": "arn:aws:sns:us-east-1:685250009713:EFIT_MongoDB_PERF_TEST_3"
        }];

    });

    // load the controller's module
    beforeEach(module(HygieiaConfig.module));

    // inject the required services and instantiate the controller
    beforeEach(
        function() {

            cloudData = {
                cloudData: sinon.stub().returns(testData)
            }

            inject(function ($rootScope, $modal, cloudData, $controller) {
                scope = $rootScope.$new();
                controller = $controller('CloudWidgetViewController', {
                    $scope: scope,
                    $modal: $modal,
                    cloudData: cloudData
                });
            })});



    describe('constructor', function () {

        describe('When I instantiate the controller', function () {
            it('Then it should be defined', function () {
                expect(controller).not.toBeUndefined();
            });
        });
    });

    describe('load()', function() {
        describe('When I call load()', function () {
            it('Then I expect AMI data to be retrieved', function() {

                //Arrange

                //Act
                var data = controller.load();

                //Assert
                var result = angular.equals( data,testData );
                expect(result).toBeTruthy();
            });
        });
    });
});
