(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('wlmonitorConfigController', wlmonitorConfigController);

    wlmonitorConfigController.$inject = ['$scope', '$q', '$uibModalInstance', 'wlmonitorData', 'modalData','collectorData','$timeout'];
    function wlmonitorConfigController($scope, $q, $uibModalInstance, wlmonitorData, modalData,collectorData,$timeout) {
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;
        var dashboardId = modalData.dashboard.id;
       console.log(widgetConfig);
       var widgetId = widgetConfig ? widgetConfig.id : null;
        ctrl.vDeployJobs = [];
        ctrl.jobDropdownDisabled = true;
        ctrl.jobDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
        ctrl.onTimeCodeSelect = onTimeCodeSelect;
        ctrl.collectorItemId = null;
        ctrl.addNewEnvironment = addNewEnvironment;
        ctrl.newenvironmentData = [];
        ctrl.deleteEnvironmentData = deleteEnvironmentData; 
        ctrl.deleteNewenvironmentData = deleteNewenvironmentData;
        ctrl.environmentData = [];
        ctrl.newDeployEnvs = [];
        ctrl.oldDeployEnvs = [];
        ctrl.submit = submit;
        collectorData.itemsByType('WLMonitor').then(processResponse);        
        function addNewEnvironment()
        {
        	ctrl.newenvironmentData.push({});
        }        
        function deleteNewenvironmentData(idx)
        {
        	ctrl.newenvironmentData.splice(idx, 1);
        	ctrl.newDeployEnvs.splice(idx, 1);
        }
        
        function deleteEnvironmentData(idx)
        {
        	ctrl.oldDeployEnvs.push(ctrl.environmentData[idx]);
        	ctrl.environmentData.splice(idx,1);        	
        }
        
        function processResponse(data) {
        	  var selectedList = _.filter(data, function(val){ return (val.enabled == true) });
		      ctrl.environmentData = selectedList;
        	  ctrl.jobDropdownDisabled = false;
              ctrl.jobDropdownPlaceholder = 'Select your application';             
              ctrl.vDeployJobs = data;
        }

        function submit(valid) {
        	console.log(ctrl.environmentData);
        	console.log(ctrl.newDeployEnvs);
        	if(valid){
        		ctrl.submitted = true; 
        		var form = document.configForm;                
                var totalList = {};
                var totalMap = {};
                var allselectedCollectorItemIds = [];
                
                console.log("ctrl.environmentData");
                console.log(ctrl.environmentData);                
                
                _.each(ctrl.environmentData, function(val) {
                	//totalMap[val.id] = "true";
                	allselectedCollectorItemIds.push(val.id);
                });
                
                
                _.each(ctrl.newDeployEnvs, function(val) {
                	console.log(val);
                	allselectedCollectorItemIds.push(val.id);
                	totalMap[val.id] = "true";
                });
                _.each(ctrl.oldDeployEnvs, function(val) {
                	totalMap[val.id] = "false";                	
                });
                
                var postObj = {
                        name: 'WLMonitor',
                        options: {
                            id: widgetConfig.options.id                        
                        },
                        componentId: modalData.dashboard.application.components[0].id,
                        collectorItemIds : totalMap,
                        allselectedCollectorItemIds : allselectedCollectorItemIds
                    };
            
                
               wlmonitorData.saveWlmonitorDetails(postObj).then(function (response) {
                	console.log(response);
                	var postObj1 = {
                            name: 'WLMonitor',
                            options: {
                                id: widgetConfig.options.id
                            },                           
    	                    componentId: modalData.dashboard.application.components[0].id    	                    
    	                }
                 $uibModalInstance.close(postObj1);
                });
        	}
        }
        
        function onTimeCodeSelect(item){		
			ctrl.newDeployEnvs.push(item);
		} 
    }
})();
