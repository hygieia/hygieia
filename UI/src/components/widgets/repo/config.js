/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module('devops-dashboard').controller('RepoConfigController',
			RepoConfigController);

	RepoConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData' ];
	function RepoConfigController(modalData, $modalInstance, collectorData) {
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		ctrl.repoOptions = [ {
			name: 'GitHub',
			value: 'GitHub'
		}, {
			name : 'Subversion',
			value : 'Subversion'
		} ];
		ctrl.repoOption = "";
		ctrl.gitBranch = "";
		ctrl.username = "";
		ctrl.password = "";

		// public variables
		ctrl.submitted = false;
		ctrl.collectors = [];
		ctrl.repoUrl = widgetConfig.options.url;

		// public methods
		ctrl.submit = submitForm;

		// Request collecters
		collectorData.collectorsByType('scm').then(processCollectorsResponse);

		function processCollectorsResponse(data) {
			console.log(data);
			ctrl.collectors = data;
		}

		/*
		 * function submitForm(valid, url) { ctrl.submitted = true; if (valid &&
		 * ctrl.collectors.length) {
		 * createCollectorItem(url).then(processCollectorItemResponse); } }
		 */

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

		/*
		 * function createCollectorItem(url) { var item = { // TODO - Remove
		 * hard-coded subversion reference when mulitple // scm collectors
		 * become available collectorId : _.findWhere(ctrl.collectors, { name :
		 * 'Subversion' }).id, options : { url : url } }; return
		 * collectorData.createCollectorItem(item); }
		 */

		function createCollectorItem(url, repoTypeName, branch) {
			var item = {};
			console.log(repoTypeName);
			console.log(branch);

			if (repoTypeName.indexOf("GitHub") != -1) {

				item = {
					collectorId: _.findWhere(ctrl.collectors, {name: 'GitHub'}).id,
					options: {
						url: url,
						branch: branch
					}
				};
			} else {
				item = {
					collectorId : _.findWhere(ctrl.collectors, {
						name: repoTypeName
					}).id,
					options: {
						url: url
					}
				};
			}
			return collectorData.createCollectorItem(item);
		}

		function processCollectorItemResponse(response) {
			var postObj = {
				name : 'repo',
				options : {
					id : widgetConfig.options.id,
					url : ctrl.repoUrl,
					branch : ctrl.gitBranch
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};

			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
	}
})();