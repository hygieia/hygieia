/**
 * Created by nmande on 4/12/16.
 */


describe('CloudWidgetViewController', function () {


    var controller;
    var scope;
    var cloudData;
    var q;
    var deferred;
    var mockInstanceData = [{"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6","cpuUtilization": 10 }, {"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6", "cpuUtilization": 20}, {"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6", "cpuUtilization": 30}];



    function retrieveTestDate(dayOffset) {

        var currentDate = new Date();
        currentDate.setDate(currentDate.getDate() + dayOffset);
        var dd = currentDate.getDate();
        var mm = currentDate.getMonth() + 1;
        var yyyy = currentDate.getFullYear();

        if (dd < 10) {
            dd = '0' + dd
        }
        if (mm < 10) {
            mm = '0' + mm
        }

        var dateString = mm + '/' + dd + '/' + yyyy;
        return new Date(dateString).getTime();
    }

    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));




    // define the mock people service
    beforeEach(function() {
        cloudData = {
            getAWSInstancesByAccount: function(accountNumber) {
                deferred = q.defer();
                deferred.resolve(mockInstanceData);
                return deferred.promise;
            }
        };
    });

    // inject the required services and instantiate the controller
    beforeEach(
        function () {
            inject(function ($rootScope, cloudData, $controller,$q) {
                scope = $rootScope.$new();

                scope.widgetConfig = {
                    options: {
                        tag: "MyTag"
                    }
                };

                controller = $controller('CloudWidgetViewController', {
                    $scope: scope,
                    cloudData: cloudData,
                    $q: $q
                });
            })
        });


    describe('calculateUtilization()', function () {
        describe('When I call calculateUtilization', function () {
            describe('And AWS instances is undefined', function () {
                it('Then I expect "N/A" to be returned', function () {

                    //Arrange
                    var undefinedInstances = undefined;
                    var expected = 'N/A';

                    //Act
                    var actual = controller.calculateUtilization(undefinedInstances);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And no AWS instances exists', function () {
                it('Then I expect "N/A" to be returned', function () {

                    //Arrange
                   var emptyInstances = [];
                    var expected = 'N/A';

                    //Act
                    var actual = controller.calculateUtilization(emptyInstances);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And AWS instances exist with the cpu utilization values', function () {
                it('Then I expect the average of the cpu utilization to be returned', function () {

                    //Arrange
                    var expected = 20;

                    //Act
                    var actual = controller.calculateUtilization(mockInstanceData);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });
        });
    });


    describe('getSortDirection()', function () {
        describe('When I call getSortDirection', function () {
            describe('And no sort key has been created', function () {
                it('Then I expect "unsorted" to be returned', function () {

                    //Arrange
                    var key = 'AMI';
                    var expected = "unsorted";

                    //Act
                    var actual = controller.getSortDirection(key);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the sort key is "-"', function () {
                it('Then I expect "sort-amount-desc" to be returned', function () {

                    //Arrange
                    var key = 'AMI';
                    var expected = "sort-amount-desc";

                    //Act
                    controller.changeSortDirection(key);
                    var actual = controller.getSortDirection(key);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the sort key is "+"', function () {
                it('Then I expect "sort-amount-asc" to be returned', function () {

                    //Arrange
                    var key = 'AMI';
                    var expected = "sort-amount-asc";

                    //Act
                    controller.changeSortDirection(key);
                    controller.changeSortDirection(key);
                    var actual = controller.getSortDirection(key);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

        });
    });



    describe('changeSortDirection()', function () {
        describe('When I call changeSortDirection', function () {
            describe('And no sort key has been created', function () {
                it('Then I expect to have one sort key added to sortType with a "-" at the front', function () {

                    //Arrange
                    var key = 'AMI';
                    var expected = ['-AMI'];

                    //Act
                    controller.changeSortDirection(key);

                    //Assert
                    expect(angular.equals(controller.sortType, expected)).toBeTruthy();
                });
            });

            describe('And one non-matching sort key exists', function () {
                it('Then I expect to have a new sort key added to the front of sortType with a "-" at the front', function () {

                    //Arrange
                    var firstKey = 'instanceID';
                    var secondKey = 'AMI';
                    var expected = ['-AMI','-instanceID'];

                    //Act
                    controller.changeSortDirection(firstKey);
                    controller.changeSortDirection(secondKey);

                    //Assert
                    expect(angular.equals(controller.sortType, expected)).toBeTruthy();
                });
            });

            describe('And one non-matching sort key exists first and a matching sort key exists second with a "-" in front', function () {
                it('Then I expect to have the matching sort key moved to the first spot with a '+' in front', function () {

                    //Arrange
                    var firstKey = 'instanceID';
                    var secondKey = 'AMI';
                    var thirdKey = 'instanceID';
                    var expected = ['+instanceID','-AMI'];

                    //Act
                    controller.changeSortDirection(firstKey);
                    controller.changeSortDirection(secondKey);
                    controller.changeSortDirection(thirdKey);

                    //Assert
                    expect(angular.equals(controller.sortType, expected)).toBeTruthy();
                });
            });


            describe('And one matching sort key exists with a "-" in the front', function () {
                it('Then I expect to the sort key updated to have "+" at the front', function () {

                    //Arrange
                    var key = 'AMI';
                    var expected = ['+AMI'];

                    //Act
                    controller.changeSortDirection(key);
                    controller.changeSortDirection(key);

                    //Assert
                    expect(angular.equals(controller.sortType, expected)).toBeTruthy();
                });
            });
        });
    });


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

    describe('checkNOTTDisabledStatus()', function () {
        describe('When I call checkNOTTDisabledStatus', function () {
            describe('And the NOTT value is set to "exclude"', function () {
                it('Then I expect "true" to be returned', function () {

                    //Arrange
                    var tags = [{"name": "Owner", "value": "joe.doe@email.com"}, {
                        "name": "visigoths:nott",
                        "value": "exclude"
                    }];
                    var expected = true;

                    //Act
                    var actual = controller.checkNOTTDisabledStatus(tags);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And the NOTT value is not set', function () {
                it('Then I expect "false" to be returned', function () {

                    //Arrange
                    var tags = [{"name": "Owner", "value": "joe.doe@email.com"}];
                    var expected = false;

                    //Act
                    var actual = controller.checkNOTTDisabledStatus(status);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });


        });
    });

    describe('checkMonitoredStatus()', function () {
        describe('When I call checkMonitoredStatus', function () {
            describe('And the status is "true"', function () {
                it('Then I expect "pass" to be returned', function () {

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
                it('Then I expect "fail" to be returned', function () {

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

    describe('checkUtilizationStatus()', function () {
        describe('When I call checkUtilizationStatus', function () {
            describe('And the status is greater than 30', function () {
                it('Then I expect "pass" to be returned', function () {

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
                it('Then I expect "fail" to be returned', function () {

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
                it('Then I expect "fail" to be returned', function () {

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


    describe('calculateCostAverage()', function () {
        describe('When I call calculateCostAverage', function () {
            describe('And all the instances are stopped', function () {
                it('Then I expect the average cost to be zero', function () {

                    //Arrange
                    var fakeData = [{"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6","cpuUtilization": 10, "stopped": true },
                        {"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6", "cpuUtilization": 20, "stopped": true}
                    ];

                    var expected = 0;

                    //Act
                    var actual = controller.calculateCostAverage(fakeData);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });

            describe('And all running instances have NOTT applied', function () {
                it('Then I expect the average cost to be the hourly rate * 12 hours', function () {

                    //Arrange
                    var fakeData = [{"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6","cpuUtilization": 10, "hourlyCost": 0.25, "tags": [{"name": "Owner", "value": "joe.doe@email.com"}]},
                        {"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6", "cpuUtilization": 20, "hourlyCost": 0.25, "tags": [{"name": "Owner", "value": "joe.doe@email.com"}]}
                    ];

                    var expected = 3; // formula = (12 hours * $0.25 * 2 instances)/2 instances

                    //Act
                    var actual = controller.calculateCostAverage(fakeData);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });


            describe('And all running instances do not have NOTT applied', function () {
                it('Then I expect the average cost to be the hourly rate * 12 hours', function () {

                    //Arrange
                    var fakeData = [{"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6","cpuUtilization": 10, "hourlyCost": 0.25, "tags": [{"name": "Owner", "value": "joe.doe@email.com"}, {
                        "name": "visigoths:nott",
                        "value": "exclude"
                    }]},
                        {"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6", "cpuUtilization": 20, "hourlyCost": 0.25, "tags": [{"name": "Owner", "value": "joe.doe@email.com"}, {
                            "name": "visigoths:nott",
                            "value": "exclude"
                        }]}
                    ];

                    var expected = 6; // formula = (24 hours * $0.25 * 2 instances)/2 instances

                    //Act
                    var actual = controller.calculateCostAverage(fakeData);

                    //Assert
                    expect(actual).toBe(expected);
                });
            });
        });
    });


    describe('toggleView()', function () {
            describe('When I call toggleView with an index that exists', function () {
                it('Then I expect the correct name to be assigned to toggledView', function () {

                    //Arrange
                    var index = 1;
                    var result = "Detail";

                    //Act
                    controller.toggleView(index);

                    //Assert
                    expect(controller.toggledView).toBe(result);
                });
            });

            describe('When I call toggleView with an index that does not exists', function () {
                it('Then I expect the default name "Overview" to be assigned to toggledView', function () {

                    //Arrange
                    var index = -50;
                    var result = "Overview";

                    //Act
                    controller.toggleView(index);

                    //Assert
                    expect(controller.toggledView).toBe(result);
                });
            });

    });
});