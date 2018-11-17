describe('EditDashboardController', function () {

  // the subject under test
  var controller;

  // controlled data to test with
  var fixedDashboard = {};
  var fixedUserData = {};
  var fixedDashboardItem = {};
  var fixedCmbdData = {};

  // keep track of our mocks
  var dashboardServiceSpy;
  var widgetManagerSpy;

  // load the controller's module
  beforeEach(module(HygieiaConfig.module), function($provide) {
    $provide.provider('widgetManager', function() {
      this.$get = function () {
        return {
          register: function (msg) {
            console.log("module");
            return 'MockRegister: ' + msg;
          }
        };
      }
    })
  } );
  beforeEach(module(HygieiaConfig.module + '.core'), function($provide) {
    $provide.provider('widgetManager', function() {
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
        dashboardServiceSpy = jasmine.createSpyObj("dashboardService", ["getDashboardTitleOrig","getBusAppToolTipText","getBusSerToolTipText"]);
        widgetManagerSpy = jasmine.createSpyObj("widgetManager", ["getWidgets"]);

        controller = $controller('EditDashboardController', {
          $uibModalInstance: {},
          $scope: scope,
          dashboardData: fixedDashboard,
          userData: fixedUserData,
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
