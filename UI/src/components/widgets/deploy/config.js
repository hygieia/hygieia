(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployConfigController', deployConfigController);

    deployConfigController.$inject = ['modalData', 'collectorData', '$uibModalInstance', '$scope'];
  
    function deployConfigController(modalData, collectorData, $uibModalInstance, $scope) {

        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.submitted = false;
        
        // When true this makes it so applications with the same id and same name that are on different servers are treated as the same entity
        ctrl.aggregateServers = false;
        ctrl.currentData = null;
        // set values from config
        if (widgetConfig) {
            if (widgetConfig.options.aggregateServers) {
                ctrl.aggregateServers = widgetConfig.options.aggregateServers;
            }
        }
      
        ctrl.ignoreRegex = '';
        if (widgetConfig.options.ignoreRegex !== undefined && widgetConfig.options.ignoreRegex !== null) {
            ctrl.ignoreRegex=widgetConfig.options.ignoreRegex;
        }

        // public methods
        ctrl.submit = submit;
        
        $scope.getDeploymentJobs = function (filter) {
        	return getDeploymentJobsRecursive([], filter, null, 0).then(processResponse);
        }
        
        loadSavedDeploymentJob();
        
        /*
         * Obtains deployment jobs using recursion when necessary. 
         * 
         * It is necessary to make additional calls when 'aggregateServers' is true since we need all like applications on different servers 
         * to be available when saving our data. To do this we compare the last item in the list from our first paged call to all subsequent data.
         * If the application name and id is the same then we keep the data in our list. Once we encounter data that is different we can cease recurision
         * since the calls are sorted.
         * 
         * Example:
         * Suppose a size of 3 is used with aggregate servers and our first REST call returns the following:
         *   Deployment A (http://deploy.instance1.com)
         *   Deployment A (http://deploy.instance2.com)
         *   Deployment B (http://deploy.instance1.com)
         *   
         * We need to make an additional rest call to see if there are any more 'Deployment B' jobs. Suppose the second REST call returns the following:
         *   Deployment B (http://deploy.instance2.com)
         *   Deployment B (http://deploy.instance3.com)
         *   Deployment C (http://deploy.instance1.com)
         *   
         * We will keep the Deployment B's returned from the REST call and ignore everything that comes after it. Since the last item in this list is different
         * than the name + id (not shown) for our original REST call we cease searching for more items.
         */
        function getDeploymentJobsRecursive(arr, filter, nameAndIdToCheck, pageNumber) {
        	return collectorData.itemsByType('deployment', {"search": filter, "size": 20, "sort": "description", "page": pageNumber}).then(function (response){
        		if (response.length > 0) {
        			arr.push.apply(arr, _(response).filter(function(d) {
    					return nameAndIdToCheck === null || nameAndIdToCheck === d.options.applicationName + "#" + d.options.applicationId;
    				}).value());
        		}
        		
        		if (ctrl.aggregateServers && response.length > 0) {
        			// The last item could have additional deployments with the same name but different servers
        			var lastItem = response.slice(-1)[0];
        			
        			var checkKey = lastItem.options.applicationName  + "#" + lastItem.options.applicationId;
        			if (nameAndIdToCheck === null || checkKey === nameAndIdToCheck) {
        				// We should check to see if the next page has the same item for our grouping
        				
        				return getDeploymentJobsRecursive(arr, filter, checkKey, pageNumber + 1);
        			}
        		}
        		return arr;
        	});
        }
        
        function processResponse(data) {
        	ctrl.currentData = data;
        	
            // If true we ignore instanceUrls and treat applications with the same id spread across 
            // multiple servers as equivalent. This allows us to fully track an application across
            // all environments in the case that servers are split by function (prod deployment servers
            // vs nonprod deployment servers)
            var multiServerEquality = ctrl.aggregateServers;

            var dataGrouped = _(data)
                .groupBy(function(d) { return (!multiServerEquality ? d.options.instanceUrl + "#" : "" ) + d.options.applicationName + d.options.applicationId; })
                .map(function(d) { return d; });

            var deploys = _(dataGrouped).map(function(deploys, idx) {
            	var firstDeploy = deploys[0];
            	
            	var name = firstDeploy.options.applicationName;
            	var group = "";
            	var ids = new Array(deploys.length);
            	for (var i = 0; i < deploys.length; ++i) {
            		var deploy = deploys[i];
            		
            		ids[i] = deploy.id;
            		
            		if (i > 0) {
            			group += '\n';
            		}
            		group += ((deploy.niceName != null) && (deploy.niceName != "") ? deploy.niceName : deploy.collector.name) + " (" + deploy.options.instanceUrl + ")";
                }
            	
                return {
                    value: ids,
                    name: name,
                    group: group
                };
            }).value();
            
            return deploys;
        }
        
        // method implementations
        function loadSavedDeploymentJob(){
        	var deployCollector = modalData.dashboard.application.components[0].collectorItems.Deployment,
            savedCollectorDeploymentJob = deployCollector ? deployCollector[0].description : null;
            if(savedCollectorDeploymentJob) { 
            	$scope.getDeploymentJobs(savedCollectorDeploymentJob).then(getDeploysCallback) 
            }
        }

        function getDeploysCallback(data) {
        	ctrl.deployJob = data[0];
        }

        function submit(valid, job) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                var postObj = {
                    name: 'deploy',
                    options: {
                        id: widgetConfig.options.id,
                        aggregateServers: form.aggregateServers.checked,
                        ignoreRegex: ctrl.ignoreRegex
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemIds: job.value
                };

                $uibModalInstance.close(postObj);
            }
        }

        $scope.reload = function() {
            processResponse(ctrl.currentData);
        };
    }
})();
