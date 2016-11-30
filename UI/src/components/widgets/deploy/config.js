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
        ctrl.ignoreRegex = 'Loading...';
        ctrl.ignoreRegexInputDisabled = true;
        if (widgetConfig.options.ignoreRegex !== undefined && widgetConfig.options.ignoreRegex !== null) {
            // If user had set regex previously, just use it
            ctrl.ignoreRegex=widgetConfig.options.ignoreRegex;
            ctrl.ignoreRegexInputDisabled = false;
        }

        // public methods
        ctrl.submit = submit;

        $q.all([getAndProcessSystemConfig(), collectorData.itemsByType('deployment')]).then(processResponse);

        function getAndProcessSystemConfig() {
            return systemConfigData.config().then(function(data) {
                var systemConfig = data;
                
                if (ctrl.ignoreRegex === 'Loading...') {
                    if (systemConfig.globalProperties && systemConfig.globalProperties.ignoreEnvironmentFailuresRegex) {
                        // if user hadn't set a regex previously, but systemconfig has one, use it
                        ctrl.ignoreRegex=systemConfig.globalProperties.ignoreEnvironmentFailuresRegex;
                    } else {
                        // if both user and systemconfig don't have a regex, just leave it blank
                        ctrl.ignoreRegex='';
                    }
                    ctrl.ignoreRegexInputDisabled = false;
                }
                
                return (systemConfig.globalProperties && systemConfig.globalProperties.multipleDeploymentServers) || false;
            });
        }
        
        function processResponse(dataA) {
            var aggregateServers = dataA[0];
            var data = dataA[1];
            
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
                        id: widgetConfig.options.id,
                        ignoreRegex: ctrl.ignoreRegex
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemIds: job.value
                };

                $modalInstance.close(postObj);
            }
        }
    }
})();
