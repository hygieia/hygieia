/**
 * Created by hyw912 on 4/12/16.
 */


describe('CloudWidgetConfigController', function () {

    var controller;
    var scope;
    var cloudData;
    var testData = [{
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

    var modalInstance;


    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));
    beforeEach(module(function($provide) {

        $provide.factory('modalData', function() {
            return {
                dashboard: function() { return ""; },
                widgetConfig: function() { return ""; }
            };
        });

        $provide.factory('cloudData', function() {
            return {
                getASV: getASV
            };

            function getASV() {
                return testData;
            };

        })
    }));

    // inject the required services and instantiate the controller
    beforeEach(
        function() {

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            inject(function ($rootScope, modalData, cloudData, $controller) {
                scope = $rootScope.$new();
                controller = $controller('CloudWidgetConfigController', {
                    $scope: scope,
                    modalData: modalData,
                    cloudData: cloudData,
                    $modalInstance: modalInstance
                });
            })});


    describe('constructor', function () {
        describe('When I instantiate the controller', function () {
            it('Then it should be defined', function () {
                expect(controller).not.toBeUndefined();
            });
        });
    });

    describe('submit()', function () {
        describe('When I submit a valid form', function () {
            it('Then I expect the modal dialog to be closed', function () {

                //Arrange
                var valid = true;

                //Act
                controller.submit(valid);

                //Assert
                expect(modalInstance.close).toHaveBeenCalled();
            });
        });

        describe('When I submit an invalid form', function () {
            it('Then I expect the modal dialog to not be closed', function () {

                //Arrange
                var valid = false;

                //Act
                controller.submit(valid);

                //Assert
                expect(modalInstance.close).not.toHaveBeenCalled();
            });
        });
    });

});

