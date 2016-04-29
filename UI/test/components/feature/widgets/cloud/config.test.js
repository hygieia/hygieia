/**
 * Created by nmande on 4/12/16.
 */


describe('CloudWidgetConfigController', function () {

    var controller;
    var scope;
    var cloudData;
    var accountData =  [
        { "name": "Development Account"},
        { "name": "Production Account"}
    ];

    var modalInstance;


    var createProviders = function() {

    }

    // load the controller's module
    beforeEach(module(HygieiaConfig.module));
    beforeEach(module(HygieiaConfig.module + '.core'));
    beforeEach(module(function($provide) {

        $provide.factory('modalData', function() {
            return {
                dashboard: {
                    application: {
                        components: [{
                            id: "myid"
                        }]
                    }
                },
                widgetConfig: {
                    options: {
                        id: "myid"
                    }
                }
            };
        });

        $provide.factory('cloudData', function() {
            return {
                getAccounts: getAccounts
            };

            function getAccounts() {
                return accountData;
            }
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
            })
        });


    describe('accountsDropdownDisabled', function() {

        describe('When account data is retrieved with one or more records', function() {
            it('Then I expect accountsDropdownDisabled to be set to "false"', function() {

                //Arrange/Act
                //Handled by the instantiation of the controller

                //Assert
                expect(controller.accountsDropdownDisabled).toBeFalsy();

            })
        });



    });


    describe('constructor', function() {
        describe('When I call the constructor', function() {
            it('Then I expect account data to be retrieved', function() {

                //Arrange/Act
                //Handled by the instantiation of the controller

                //Assert
                expect(angular.equals(controller.accounts, accountData)).toBeTruthy();

            })
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

