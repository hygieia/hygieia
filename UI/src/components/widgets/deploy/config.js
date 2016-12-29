(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployConfigController', deployConfigController);

    deployConfigController.$inject = ['modalData', 'collectorData', 'systemConfigData', '$modalInstance', '$q'];
    function deployConfigController(modalData, collectorData, systemConfigData, $modalInstance, $q) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        // ctrl.deployJob;
        ctrl.deployJobs = [ ];
        ctrl.jobDropdownDisabled = true;
        ctrl.jobDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;

        // public methods
        ctrl.submit = submit;

        $q.all([systemConfigData.config(), collectorData.itemsByType('deployment')]).then(processResponse);

        function processResponse(dataA) {
        	var systemConfig = dataA[0];
        	var data = dataA[1];
        	
        	var aggregateServers = (systemConfig.globalProperties && systemConfig.globalProperties.multipleDeploymentServers) || false;
        	
            var worker = {
                getDeploys: getDeploys
            };

            
            function getDeploys(data, currentCollectorItemIds, cb) {
                var selectedIndex = null;
                
                // If true we ignore instanceUrls and treat applications with the same id spread across 
                // multiple servers as equivalent. This allows us to fully track an application across
                // all environments in the case that servers are split by function (prod deployment servers
                // vs nonprod deployment servers)
                var multiServerEquality = aggregateServers;
                var dataGrouped = dataGrouped = _(data)
                	.groupBy(function(d) { return (!multiServerEquality ? d.options.instanceUrl + "#" : "" ) + d.options.applicationId; })
                	.map(function(d) { return d; });
                
                var deploys = _(dataGrouped).map(function(deploys, idx) {
                	var firstDeploy = deploys[0];
                	
                	var name = "";
                	var group = "";
                	var ids = new Array(deploys.length);
                	for (var i = 0; i < deploys.length; ++i) {
                		var deploy = deploys[i];
                		
                		ids[i] = deploy.id;
                		
                		if (_.contains(currentCollectorItemIds, deploy.id)) {
                            selectedIndex = idx;
                        }
                		
                		if (i > 0) {
                			name += ', ';
                		}
                		name += ((deploy.niceName != null) && (deploy.niceName != "") ? deploy.niceName : deploy.collector.name);
                    }
                	
                	group = name;
                	name += '-' + firstDeploy.options.applicationName;
                	
                    return {
                        value: ids,
                        name: name,
                        group: group
                    };
                }).value();

                cb({
                    deploys: deploys,
                    selectedIndex: selectedIndex
                });
            }

            var deployCollectorItems = modalData.dashboard.application.components[0].collectorItems.Deployment;
            var selectedIds = [];
            if (deployCollectorItems) {
            	selectedIds = _.map(deployCollectorItems, function(ci) { return ci.id } )
            }
            
            worker.getDeploys(data, selectedIds, getDeploysCallback);
        }

        function getDeploysCallback(data) {
            //$scope.$apply(function() {
                ctrl.jobDropdownDisabled = false;
                ctrl.jobDropdownPlaceholder = 'Select your application';
                ctrl.deployJobs = data.deploys;

                if(data.selectedIndex !== null) {
                    ctrl.deployJob = data.deploys[data.selectedIndex];
                }
            //});
        }


        function submit(valid, job) {
            ctrl.submitted = true;

            if (valid) {
                var form = document.configForm;
                var postObj = {
                    name: 'deploy',
                    options: {
                        id: widgetConfig.options.id
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemIds: job.value
                };

                $modalInstance.close(postObj);
            }
        }
    }
})();
