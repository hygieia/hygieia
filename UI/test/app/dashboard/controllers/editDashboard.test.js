describe('EditDashboardController', function () {

  // the subject under test
  var controller;

  // controlled data to test with
  var fixedDashboard = {
    type: "widget",
    configurationItemBusServName: "serviceName",
    configurationItemBusAppName: "AppName",
    scoreEnabled: false,
    id: "id"
  };
  var fixedDashboardItem = {};
  var fixedCmbdData = {};

  // keep track of our mocks
  var dashboardServiceSpy;
  var widgetManagerSpy;
  var userServiceSpy;
  var dashboardSpy;
  var usersSpy;

  // load the controller's module
  beforeEach(module(HygieiaConfig.module), function ($provide) {
    $provide.provider('widgetManager', function () {
      this.$get = function () {
        return {
          register: function (msg) {
            console.log("module");
            return 'MockRegister: ' + msg;
          }
        };
      }
    })
  });
  beforeEach(module(HygieiaConfig.module + '.core'), function ($provide) {
    $provide.provider('widgetManager', function () {
      this.$get = function () {
        return {
          register: function (msg) {
            console.log("core");
            return 'MockRegister2: ' + msg;
          }
        };
      }
    })
  });


  // inject mocks etc into the subject under test
  beforeEach(
    function () {
      inject(function ($rootScope, $q, $controller) {
        var scope = $rootScope.$new();
        dashboardServiceSpy = jasmine.createSpyObj("dashboardService", ["getDashboardTitleOrig", "getBusAppToolTipText", "getBusSerToolTipText"]);
        widgetManagerSpy = jasmine.createSpyObj("widgetManager", ["getWidgets"]);
        userServiceSpy = jasmine.createSpyObj("userservice", ["getUsername", "getAuthType"]);
        dashboardSpy = jasmine.createSpyObj("dashboardData",["owners","detail"]);
        usersSpy = jasmine.createSpyObj("userData",["getAllUsers"]);

        // setup user
        userServiceSpy.getUsername.and.returnValue("Steve");
        userServiceSpy.getAuthType.and.returnValue("owner");

        //setup dashboard
        dashboardSpy.owners.and.returnValue($q.when('["Steve"]'));
        dashboardSpy.detail.and.returnValue($q.when('dashboard'));

        //setup users
        usersSpy.getAllUsers.and.returnValue($q.when('["Steve"]'));

        controller = $controller('EditDashboardController', {
          $uibModalInstance: {},
          $scope: scope,
          dashboardData: dashboardSpy,
          userData: usersSpy,
          userService: userServiceSpy,
          dashboardItem: fixedDashboardItem,
          cmdbData: fixedCmbdData,
          dashboardService: dashboardServiceSpy,
          widgetManager: widgetManagerSpy,
          $q: $q
        });
      });
    })

  describe("create controller", function () {
    it("should have setup the controller", function () {

    })
  })
});
