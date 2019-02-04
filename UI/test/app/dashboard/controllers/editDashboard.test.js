describe('EditDashboardController', function () {

  // the subject under test
  var controller;
  var $templateCache;
  var $scope;
  var $compile;

  // controlled data to test with
  var fixedDashboard = {
    template: "widgets",
    configurationItemBusServName: "serviceName",
    configurationItemBusAppName: "AppName",
    scoreEnabled: false,
    id: "id",
    owner: "Steve",
    title: "Dashboard01",
    activeWidgets: [
      {type: "build", title: "build01"},
      {type: "build", title: "build02"},
      {type: "code", title: "code03"}
    ],
    widgets: [
      {id: "01", name: "build01", type: "build", collectorItemIds: ["02", "03", "04"]},
      {id: "02", name: "build02", type: "build", collectorItemIds: ["04"]},
      {id: "03", name: "code03", type: "code", collectorItemIds: ["05"]}
    ]
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
      inject(function ($httpBackend,$rootScope, _$templateCache_, $q, $controller, _$compile_) {
        $templateCache = _$templateCache_;
        $scope = $rootScope.$new();
        dashboardServiceSpy = jasmine.createSpyObj("dashboardService", ["getDashboardTitleOrig", "getBusAppToolTipText", "getBusSerToolTipText"]);
        widgetManagerSpy = jasmine.createSpyObj("widgetManager", ["getWidgets"]);
        userServiceSpy = jasmine.createSpyObj("userservice", ["getUsername", "getAuthType"]);
        dashboardSpy = jasmine.createSpyObj("dashboardData", ["owners", "detail"]);
        usersSpy = jasmine.createSpyObj("userData", ["getAllUsers"]);

        // setup user
        userServiceSpy.getUsername.and.returnValue("Steve");
        userServiceSpy.getAuthType.and.returnValue("owner");

        //setup dashboard
        dashboardSpy.owners.and.returnValue($q.when('["Steve"]'));
        dashboardSpy.detail.and.returnValue($q.when(fixedDashboard));

        //setup users
        usersSpy.getAllUsers.and.returnValue($q.when('["Steve"]'));

        controller = $controller('EditDashboardController', {
          $uibModalInstance: {},
          $scope: $scope,
          dashboardData: dashboardSpy,
          userData: usersSpy,
          userService: userServiceSpy,
          dashboardItem: fixedDashboardItem,
          cmdbData: fixedCmbdData,
          dashboardService: dashboardServiceSpy,
          widgetManager: widgetManagerSpy,
          $q: $q
        });
        // expect the view to be got
        $httpBackend.whenGET('app/dashboard/views/site.html').respond("TEXT");
        // trigger angular digest to resolve all those promises
        $rootScope.$apply();
        $compile = _$compile_;
      });
    });

  describe("create controller", function () {
    it("should have setup the controller", function () {
      expect(controller.selectWidgetsDisabled).toBe(false);
      expect(Object.keys(controller.activeWidgets).length).toBe(3);
      expect(Object.values(controller.activeWidgets)).toEqual([
        {type: "build", title: "build01", width:4, height:1, order:0},
        {type: "build", title: "build02", width:4, height:1, order:1},
        {type: "code", title: "code03", width:4, height:1, order:2}]);
      // I suspect we'll have to do something with the widgets as well..
      expect(Object.keys(controller.widgetSelections)).toEqual(["build01","build02","code03"]);
      expect(Object.values(controller.widgetSelections).length).toBe(3);
    })
  })

  describe("modify widgets", function() {
    it("should remove the active widget", function() {
      controller.removeWidget("build02");

      expect(Object.keys(controller.activeWidgets).length).toBe(2);
      expect(Object.values(controller.activeWidgets)).toEqual([
        {type: "build", title: "build01", width:4, height:1, order:0},
        {type: "code", title: "code03", width:4, height:1, order:2}
      ]);
      expect(Object.keys(controller.widgetSelections)).toEqual(["build01","code03"]);
    })
  })

  describe("template", function() {
    it("should render", function() {
      var html = $templateCache.get('editDashboard.html');
      $scope.$digest();
      var view = $compile(angular.element(html))($scope);
    })
  })
});
