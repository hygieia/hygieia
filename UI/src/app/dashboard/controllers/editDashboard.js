/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('EditDashboardController', EditDashboardController)
        .filter('ownerFilter', function() {
        	return function(users, owners) {
        		var result = []
        		angular.forEach(users, function(user) {
        			var isOwner = false
        			angular.forEach(owners, function(owner) {
        				if(user.username === owner.username && user.authType === owner.authType) {
        					isOwner = true
        				}
        			})
        			
        			if (!isOwner) {
        				result.push(user)
        			}
        		})
        		
        		return result
        	}
        });

    EditDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'userData', 'userService', 'dashboardItem', '$scope', '$q', 'cmdbData', 'dashboardService','widgetManager'];
    function EditDashboardController($uibModalInstance, dashboardData, userData, userService, dashboardItem, $scope, $q, cmdbData, dashboardService,widgetManager) {

        var ctrl = this;
        // setup cell heights
        $scope.options = {
            cellHeight: 200,
            verticalMargin: 10
        };

        // public variables
        ctrl.dashboardType = dashboardItem.type;
        ctrl.configurationItemBusServ = dashboardItem.configurationItemBusServName;
        ctrl.configurationItemBusApp = dashboardItem.configurationItemBusAppName;
        ctrl.tabs = [
            { name: "Dashboard Title"},
            { name: "Business Service/ Application"},
            { name: "Owner Information"},
            { name: "Widget Management"},
            { name: "Score"}

        ];
        ctrl.tabView = ctrl.tabs[0].name;
        ctrl.scoreSettings = {
            scoreEnabled : !!dashboardItem.scoreEnabled,
            scoreDisplay : dashboardItem.scoreDisplay
        };

        // public methods
        ctrl.submit = submit;
        ctrl.submitBusServOrApp = submitBusServOrApp;
        ctrl.ownerFormSubmit = ownerFormSubmit;
        ctrl.getConfigItem = getConfigItem;
        ctrl.getDashboardTitle = getDashboardTitle;
        ctrl.getBusAppToolText = getBusAppToolText;
        ctrl.getBusSerToolText = getBusSerToolText;
        ctrl.tabToggleView = tabToggleView;
        ctrl.isValidBusServName = isValidBusServName;
        ctrl.isValidBusAppName = isValidBusAppName;
        ctrl.saveWidgets = saveWidgets;
        ctrl.onConfigurationItemBusAppSelect = onConfigurationItemBusAppSelect;
        ctrl.submitScoreSettings = submitScoreSettings;
        ctrl.removeWidget = removeWidget;
        ctrl.addWidget = addWidget;

        ctrl.validBusServName = isValidBusServName();
        ctrl.validBusAppName = isValidBusAppName();
        ctrl.dashboardTitle = getDashboardTitle();

        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();

        dashboardData.owners(dashboardItem.id).then(processOwnerResponse);

        dashboardData.detail(dashboardItem.id).then(processDashboardDetail);


        function processDashboardDetail(response){
            var data = response;
          // collection to hold selected widgets
          ctrl.widgetSelections=[];
          // collection to hold active widgets
          ctrl.activeWidgets=[];
          ctrl.widgets = widgetManager.getWidgets();
          ctrl.maxActiveCounter=0;
          if(response.template =='widgets'){
              ctrl.selectWidgetsDisabled = false;
              response.activeWidgets.forEach(function(activeWidget, index){
                  activeWidget.width=4;
                  activeWidget.height=1;
                  activeWidget.order=index;
                  ctrl.maxActiveCounter= Math.max(ctrl.maxActiveCounter,activeWidget.title.match(/\d+$/)[0]);
                  ctrl.activeWidgets[activeWidget.title]=activeWidget;
                });
              response.widgets.forEach(function(widgetConfig){
                  // not sure we need to track this do we?
                  if (widgetConfig.name) {
                      ctrl.widgetSelections[widgetConfig.name] = widgetConfig;
                  }
                })
            }else{
              // this section is for template dashboards. Should we allow this to be edited?
              ctrl.selectWidgetsDisabled = true;
              _.map(ctrl.widgets, function (value, key) {
                  // this isn't true. Not all widgets are active!
                    ctrl.activeWidgets.push(key);
                });
            }
        }

        function processUserResponse(response) {
            $scope.users = response.data;
        }

        function processOwnerResponse(response) {
        	$scope.owners = response;
        	userData.getAllUsers().then(processUserResponse);
        }
        
        $scope.isActiveUser = function(user) {
            if(user.authType === ctrl.authType && user.username === ctrl.username) {
                return true;
            }
            return false;
        }

        $scope.promoteUserToOwner = function(user) {
            var index = $scope.users.indexOf(user);
        	if (index > -1) {
        		$scope.owners.push(user)
        	}
        }

        $scope.demoteUserFromOwner = function(user) {
        	var index = $scope.owners.indexOf(user);
        	if (index > -1) {
        		$scope.owners.splice(index, 1)
        	}
        }

        function submit(form) {
            form.dashboardTitle.$setValidity('renameError', true);
            if (form.$valid) {
                renameSubmit()
                    .catch(function(error){
                    	$scope.error = error.data
                    });
            } else {
                form.dashboardTitle.$setValidity('renameError', false);
            }
        }

        function renameSubmit() {
	    	return $q.when(dashboardData.rename(dashboardItem.id, document.cdf.dashboardTitle.value))
	    	         .then(function() {
                         $uibModalInstance.close();
                     });
        }
        function ownerFormSubmit(form) {

            if (form.$valid) {
                ownerSubmit()
                    .catch(function(error){
                        $scope.error = error.data
                    });
            }
        }
        function ownerSubmit() {

            return $q.when(dashboardData.updateOwners(dashboardItem.id, prepareOwners($scope.owners)))
                .then(function() {
                    $uibModalInstance.close();
                });
        }

        function prepareOwners(owners) {
        	var putData = []
        	
        	owners.forEach(function(owner) {
        		putData.push({username: owner.username, authType: owner.authType})
        	})
        	
        	return putData
        }

        function submitBusServOrApp(form) {
            resetFormValidation(form);
            if (form.$valid) {
                var submitData = {
                    configurationItemBusServName: document.formBusinessService.configurationItemBusServ ? document.formBusinessService.configurationItemBusServ.value : null,
                    configurationItemBusAppName:  document.formBusinessService.configurationItemBusApp ?  document.formBusinessService.configurationItemBusApp.value : null
                };
                dashboardData
                    .updateBusItems(dashboardItem.id,submitData)
                    .success(function (data) {
                        $uibModalInstance.close();
                    })
                    .error(function (data) {
                        if(data){
                            ctrl.dupErroMessage = data;
                        }

                        form.configurationItemBusServ.$setValidity('dupBusServError', false);
                        form.configurationItemBusApp.$setValidity('dupBusAppError', false);
                    });
            }

        }

        function getConfigItem(type ,filter) {
            return cmdbData.getConfigItemList(type, {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }
        function getDashboardTitle(){
            return  dashboardService.getDashboardTitleOrig(dashboardItem);
        }

        function getBusAppToolText(){
            return dashboardService.getBusAppToolTipText();
        }

        function getBusSerToolText(){
            return dashboardService.getBusSerToolTipText();
        }
        function tabToggleView(index) {
            ctrl.dupErroMessage = "";
            ctrl.tabView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };
        function resetFormValidation(form){
            ctrl.dupErroMessage = "";
            form.configurationItemBusServ.$setValidity('dupBusServError', true);
            if(form.configurationItemBusApp){
                form.configurationItemBusApp.$setValidity('dupBusAppError', true);
            }

        }
        function isValidBusServName(){
            var valid = true;
            if(dashboardItem.configurationItemBusServName != undefined && !dashboardItem.validServiceName){
                valid = false;
            }
            return valid;
        }
        function isValidBusAppName(){
            var valid = true;
            if(dashboardItem.configurationItemBusAppName != undefined && !dashboardItem.validAppName){
                valid = false;
            }
            return valid;
        }

        function removeWidget(title) {
            delete ctrl.activeWidgets[title];
            delete ctrl.widgetSelections[title];
        }

        function addWidget(type) {
            ctrl.maxActiveCounter++;
            var title = type+ctrl.maxActiveCounter;
            ctrl.activeWidgets[title] = {type:type, title: title, width: 4, height: 1, order: ctrl.maxActiveCounter}
        }

        // Save template - after edit
        function saveWidgets(form) {
            findSelectedWidgets();
            if(form.$valid ){
                var active = [];
                Object.values(ctrl.activeWidgets).forEach(function(widget){
                   active.push({title:widget.title,type:widget.type});
                });
                var submitData = {
                    activeWidgets: active
                };
                dashboardData
                    .updateDashboardWidgets(dashboardItem.id,submitData)
                    .success(function (data) {
                        $uibModalInstance.close();
                    })
                    .error(function (data) {
                        var msg = 'An error occurred while editing dashboard';
                        swal(msg);
                    });
            }
        }

        // find selected widgets and add it to collection
        function findSelectedWidgets(){
            ctrl.selectedWidgets = [];
            for(var selectedWidget in ctrl.widgetSelections){
                var s = ctrl.widgetSelections[selectedWidget];
                if(s){
                    ctrl.selectedWidgets.push(selectedWidget);
                }
            }
        }

        function onConfigurationItemBusAppSelect(value){
            ctrl.configurationItemBusApp = value;
        }

        function submitScoreSettings(form) {
            if(form.$valid ){
                dashboardData
                    .updateDashboardScoreSettings(dashboardItem.id, ctrl.scoreSettings.scoreEnabled, ctrl.scoreSettings.scoreDisplay)
                    .success(function (data) {
                        $uibModalInstance.close();
                    })
                    .error(function (data) {
                        var msg = 'An error occurred while editing dashboard';
                        swal(msg);
                    });
            }
        }
    }
})();
