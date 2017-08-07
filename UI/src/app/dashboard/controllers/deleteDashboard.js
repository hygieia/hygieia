/**
 * Controller for the modal popup when deleting
 * a  dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('DeleteDashboardController', DeleteDashboardController)
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

    DeleteDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'userData', 'userService', 'dashboardItem', '$scope', '$q', 'cmdbData', 'dashboardService'];
    function DeleteDashboardController($uibModalInstance, dashboardData, userData, userService, dashboardItem, $scope, $q, cmdbData, dashboardService) {

        var ctrl = this;

        // public variables
        ctrl.dashboardType = dashboardItem.type;
   
        // public methods
    
        ctrl.getConfigItem = getConfigItem;
       
        ctrl.getDashboardTitle = getDashboardTitle;
     
       
       ctrl.dashboardTitle = getDashboardTitle();
        
        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();
        
        ctrl.deleteDashboard = deleteDashboard;
        

        dashboardData.owners(dashboardItem.id).then(processOwnerResponse);
        
        
             
       
        function deleteDashboard(item) {
            var id = item.id;
            dashboardData.delete(id).then(function () {
            	 _.remove(dashboardItem[1], {id: id});
                 _.remove(dashboardItem[2], {id: id});
           
            }, function(response) {
                var msg = 'An error occurred while deleting the dashboard';

                if(response.status > 204 && response.status < 500) {
                    msg = 'The Team Dashboard is currently being used by a Product Dashboard/s. You cannot delete at this time.';
                }

                swal(msg);
            });
            $uibModalInstance.dismiss();
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



        function getConfigItem(type ,filter) {
            return cmdbData.getConfigItemList(type, {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }
       
        function getDashboardTitle(){
            return  dashboardService.getDashboardTitleOrig(dashboardItem[0]);
        }
     

    }
})();
