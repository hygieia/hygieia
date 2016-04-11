/**
 * Created by hyw912 on 4/11/16.
 */

describe('CapOneTemplateController', function () {

    var controller;
    var scope;

    // load the controller's module
    beforeEach(module(HygieiaConfig.module));

    // inject the required services and instantiate the controller
    beforeEach(inject(function ($rootScope, $controller) {

        scope = $rootScope.$new();
        controller = $controller('CapOneTemplateController', {$scope: scope});
    }));


    describe('Constructor', function () {

        describe('When I instantiate the controller', function () {
            it('Then it should be defined', function () {
                expect(controller).not.toBeUndefined();
            });
        });
    });

    describe('ToggleView', function () {
        describe('When I call with an index that exists', function () {
            it('Then I expect the correct name to be assigned to widgetView', function () {

                //Arrange
                var index = 1;
                var result = "Pipeline";

                //Act
                controller.toggleView(index);

                //Assert
                expect(controller.widgetView).toBe(result);
            });
        });

    });
});
