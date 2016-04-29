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

    function retrieveTestDate(dayOffset) {
        var currentDate = new Date();
        currentDate.setDate(currentDate.getDate() + dayOffset);
        var dd = currentDate.getDate();
        var mm = currentDate.getMonth()+1;
        var yyyy = currentDate.getFullYear();

        if(dd<10) { dd='0'+dd }
        if(mm<10) { mm='0'+mm }

       return mm+'/'+dd+'/'+yyyy;
    }

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


    describe('checkImageAgeStatus()', function() {
        describe('When I call checkImageAgeStatus', function () {
            describe('And the expiration date is earlier than today', function () {
                it('Then I expect "fail" to be returned', function() {

                    //Arrange
                    var expirationDate = retrieveTestDate(-10);
                    var expected = "fail";

                    //Act
                    var actual = controller.checkImageAgeStatus(expirationDate);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the expiration date is today', function () {
                it('Then I expect "warn" to be returned', function() {

                    //Arrange
                    var expirationDate = retrieveTestDate(0);
                    var expected = "warn";

                    //Act
                    var actual = controller.checkImageAgeStatus(expirationDate);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the expiration date is 15 days from now', function () {
                it('Then I expect "warn" to be returned', function() {

                    //Arrange
                    var expirationDate = retrieveTestDate(15);
                    var expected = "warn";

                    //Act
                    var actual = controller.checkImageAgeStatus(expirationDate);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the expiration date is 16 days from now', function () {
                it('Then I expect "pass" to be returned', function() {

                    //Arrange
                    var expirationDate = retrieveTestDate(16);
                    var expected = "pass";

                    //Act
                    var actual = controller.checkImageAgeStatus(expirationDate);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });
        });
    });

    describe('checkNOTTStatus()', function() {
        describe('When I call checkNOTTStatus', function () {
            describe('And the status is "Excluded"', function () {
                it('Then I expect "fail" to be returned', function() {

                    //Arrange
                    var status = "Excluded";
                    var expected = "fail";

                    //Act
                    var actual = controller.checkNOTTStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the status is not "Excluded"', function () {
                it('Then I expect "pass" to be returned', function() {

                    //Arrange
                    var status = "notExcluded";
                    var expected = "pass";

                    //Act
                    var actual = controller.checkNOTTStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });


        });
    });

    describe('checkMonitoredStatus()', function() {
        describe('When I call checkMonitoredStatus', function () {
            describe('And the status is "true"', function () {
                it('Then I expect "pass" to be returned', function() {

                    //Arrange
                    var status = true;
                    var expected = "pass";

                    //Act
                    var actual = controller.checkMonitoredStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the status is "false"', function () {
                it('Then I expect "fail" to be returned', function() {

                    //Arrange
                    var status = false;
                    var expected = "fail";

                    //Act
                    var actual = controller.checkMonitoredStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });


        });
    });

    describe('checkUtilizationStatus()', function() {
        describe('When I call checkUtilizationStatus', function () {
            describe('And the status is greater than 30', function () {
                it('Then I expect "pass" to be returned', function() {

                    //Arrange
                    var status = 31;
                    var expected = "pass";

                    //Act
                    var actual = controller.checkUtilizationStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the status is less than 30', function () {
                it('Then I expect "fail" to be returned', function() {

                    //Arrange
                    var status = 29;
                    var expected = "fail";

                    //Act
                    var actual = controller.checkUtilizationStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the status is 30', function () {
                it('Then I expect "fail" to be returned', function() {

                    //Arrange
                    var status = 30;
                    var expected = "fail";

                    //Act
                    var actual = controller.checkUtilizationStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

        });
    });

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
