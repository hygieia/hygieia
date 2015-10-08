(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('cloudConfigController', cloudConfigController);

    cloudConfigController.$inject = ['$scope','modalData', 'cloudData', '$modalInstance', 'collectorData'];
    function cloudConfigController($scope, modalData, cloudData, $modalInstance, collectorData) {
        var ctrl = this;


        ctrl.services = [{'name': 'AWS'}];
        ctrl.toolsDropdownDisabled = true;
        ctrl.toolsDropdownPlaceholder = 'Select a service';
        ctrl.submitted = false;
        ctrl.validAccess = false;

		var widgetConfig = modalData.widgetConfig;

        // public methods
        ctrl.showError = showError;
        ctrl.submit = submitConfig;
		$scope.alerts = [];


		$scope.closeAlert = function(index) {
			$scope.alerts.splice(index, 1);
			if (signup.userCreated) {
				$location.path("/");
			}
		};
		if (!widgetConfig.options.cloud) {
			ctrl.service="AWS";
		}
		else
		{
			var myindex = 0;

			for (var v = 0; v < ctrl.services.length; v++) {
				if (ctrl.services[v].name == widgetConfig.options.cloud.name) {
					myindex = v;
				}
			}
			ctrl.service=ctrl.services[myindex];
		}

		// Request collecters
		collectorData.collectorsByType('cloud').then(processCollectorsResponse);

		function processCollectorsResponse(data) {
			console.log(data);
			ctrl.collectors = data;
		}

        function showError(element) {
            // tell the view whether or not to show errors only once the form has been submitted once
            return element.$invalid && ctrl.submitted;
        }

        function submitConfig(valid) {
            ctrl.submitted = true;

            if (valid) {
                //make get request to validate access and secret key
                var item = {};

				item = {
					collectorId: _.findWhere(ctrl.collectors, {name: 'AWSCloud'}).id,
					options: {
						accessKey: ctrl.accessKey,
						secretKey: ctrl.secretKey,
						cloudProvider: ctrl.service
					}
				};
				console.log(item);
                cloudData.saveConfig(item).then(processCollectorItemResponse);
            }
        }

        /**
         *
         function submitForm(valid, url, repoType) {
			ctrl.submitted = true;
			if (valid && ctrl.collectors.length) {
				if (repoType == 'GitHub (public)') {
					createCollectorItem(url, repoType, ctrl.gitBranch).then(
							processCollectorItemResponse);
				} else if (repoType == 'GitHub (private)') {
					var httpReplace = ("http://").concat(ctrl.username).concat(
							":").concat(ctrl.password).concat("@");
					var httpsReplace = ("https://").concat(ctrl.username)
							.concat(":").concat(ctrl.password).concat("@");
					var url2 = url.replace("http://", httpReplace);
					var url3 = url2.replace("https://", httpsReplace);
					createCollectorItem(url3, repoType, ctrl.gitBranch).then(
							processCollectorItemResponse);
				} else {
					createCollectorItem(url, repoType.name, ctrl.gitBranch).then(
							processCollectorItemResponse);
				}
			}
		}
         function createCollectorItem(url, repoTypeName, branch) {
			var item = {};

			if (repoTypeName.indexOf("GitHub") != -1) {

				item = {
					collectorId: _.findWhere(ctrl.collectors, {name: 'GitHub'}).id,
					options: {
						scm: 'Github',
						url: url,
						branch: branch
					}
				};
			} else {
				console.log(repoTypeName);
				item = {
					collectorId : _.findWhere(ctrl.collectors, { name: 'Subversion' }).id,
					options: {
						scm: 'Subversion',
						url: url
					}
				};
			}
			return collectorData.createCollectorItem(item);
		}

         function processCollectorItemResponse(response) {
			var postObj = {
				name : "repo",
				options : {
					id : widgetConfig.options.id,
					scm : ctrl.repoOption,
					url : ctrl.repoUrl,
					branch : ctrl.gitBranch
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};

			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
         * @param data
         * @returns {*}
         */
        function createCollectorItem(data) {
            //TODO: a collector item needs to be created to follow the same format as other widget
            //var item = {collectorId: ,
            //name:
            //options};
            return collectorData.createCollectorItem(item);
        }

        function createCollectorItem(data) {
            var item = {
                // TODO - Remove hard-coded versionone reference when mulitple
                // scm collectors become available
                collectorId: _.findWhere(ctrl.collectors, {
                    name: "VersionOne"
                }).id,
                options: {
                    data: data
                }
            };
            return collectorData.createCollectorItem(item);
        }

		function processCollectorItemResponse(response) {
			ctrl.validAccess = true;
			var postObj = {
				name : "cloud",
				options : {
					id : widgetConfig.options.id,
					accessKey : ctrl.accessKey,
					secretKey : ctrl.secretKey,
					cloudProvider : ctrl.service
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};

			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
    }
})();
