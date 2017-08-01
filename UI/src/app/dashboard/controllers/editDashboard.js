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

    EditDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'userData', 'userService', 'dashboardItem', '$scope', '$q', 'cmdbData', 'dashboardService'];
    function EditDashboardController($uibModalInstance, dashboardData, userData, userService, dashboardItem, $scope, $q, cmdbData, dashboardService) {

        var ctrl = this;

        // public variables
        ctrl.dashboardType = dashboardItem.type;
        ctrl.configurationItemBusServ = dashboardItem.configurationItemBusServName;
        ctrl.configurationItemBusApp = dashboardItem.configurationItemBusAppName;
        ctrl.tabs = [
            { name: "Dashboard Title"},
            { name: "Business Service/ Application"},
            { name: "Owner Information"}

        ];
        ctrl.tabView = ctrl.tabs[0].name;

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

        ctrl.validBusServName = isValidBusServName();
        ctrl.validBusAppName = isValidBusAppName();
        ctrl.dashboardTitle = getDashboardTitle();

        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();

        dashboardData.owners(dashboardItem.id).then(processOwnerResponse);

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
    }
})();
