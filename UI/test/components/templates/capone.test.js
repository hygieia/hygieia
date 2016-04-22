/**
 * Created by nmande on 4/11/16.
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




    describe('toggleView()', function () {
       describe('When I call toggleView with an index that exists', function () {
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

        describe('When I call toggleView with an index that does not exists', function () {
            it('Then I expect the default name "Widget" to be assigned to widgetView', function () {

                //Arrange
                var index = -50;
                var result = "Widget";

                //Act
                controller.toggleView(index);

                //Assert
                expect(controller.widgetView).toBe(result);
            });
        });

    });


});
