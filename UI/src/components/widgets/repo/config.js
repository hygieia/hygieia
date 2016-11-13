/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('RepoConfigController',
			RepoConfigController);

	RepoConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData' ];
	function RepoConfigController(modalData, $modalInstance, collectorData) {
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		ctrl.repoOptions = [{
			name: 'GitHub',
			value: 'GitHub'
		}, {
			name: 'Subversion',
			value: 'Subversion'
		},{
			name: 'Bitbucket',
			value: 'Bitbucket'
		}, {
			name: 'Gitlab',
			value: 'Gitlab'
		}];

//console.log(JSON.stringify(widgetConfig)); //"{"options":{"id":"repo0"}}"
//		console.log(JSON.stringify(widgetConfig.options.id));

		if (!widgetConfig.options.scm) {
			ctrl.repoOption="";
		}
		else
		{
			var myindex;

			for (var v = 0; v < ctrl.repoOptions.length; v++) {
				if (ctrl.repoOptions[v].name == widgetConfig.options.scm.name) {
					myindex = v;
				}
			}
			ctrl.repoOption=ctrl.repoOptions[myindex];
		}

		ctrl.repoUrl = widgetConfig.options.url;
		ctrl.gitBranch = widgetConfig.options.branch;
		ctrl.repouser = widgetConfig.options.userID;
		ctrl.repopass = widgetConfig.options.password;

		// public variables
		ctrl.submitted = false;
		ctrl.collectors = [];

		// public methods
		ctrl.submit = submitForm;

		// Request collecters
		collectorData.collectorsByType('scm').then(processCollectorsResponse);

		function processCollectorsResponse(data) {
			ctrl.collectors = data;
		}

		/*
		 * function submitForm(valid, url) { ctrl.submitted = true; if (valid &&
		 * ctrl.collectors.length) {
		 * createCollectorItem(url).then(processCollectorItemResponse); } }
		 */
		function submitForm(form) {
			ctrl.submitted = true;
			if (form.$valid && ctrl.collectors.length) {

				//there is an existing repo and nothing was changed
				if (widgetConfig.options.scm) {
					if (ctrl.repoOption.name === widgetConfig.options.scm.name &&
						ctrl.repoUrl === widgetConfig.options.url &&
						ctrl.gitBranch === widgetConfig.options.branch &&
						ctrl.repouser === widgetConfig.options.userID &&
						ctrl.repopass === widgetConfig.options.password) {
						$modalInstance.close();
						return;
					}
				}

				if (ctrl.repopass) {
					if (ctrl.repopass === widgetConfig.options.password) {
						//password is unchanged in the form so don't encrypt it again
						try {
							createCollectorItem().then(processCollectorItemResponse);
						} catch (e) {
							console.log(e);
						}
					} else {
						collectorData.encrypt(ctrl.repopass).then(function (response) {
							if (response === 'ERROR') {
								form.repopass.$setValidity('errorEncryptingPassword', false);
								return;
							}
							ctrl.repopass = response;
							try {
								createCollectorItem().then(processCollectorItemResponse);
							} catch (e) {
								console.log(e);
							}
						});
					}
				} else {
					createCollectorItem().then(processCollectorItemResponse);
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

		function createCollectorItem() {
			var item = {};

			if (ctrl.repoOption.name.indexOf("GitHub") != -1) {

				item = {
					collectorId: _.findWhere(ctrl.collectors, {name: 'GitHub'}).id,
					options: {
						scm: 'Github',
						url: ctrl.repoUrl,
						branch: ctrl.gitBranch,
						userID: ctrl.repouser,
						password: ctrl.repopass
					}
				};
			} else if (ctrl.repoOption.name.indexOf("Bitbucket") != -1) {

				item = {
					collectorId: _.findWhere(ctrl.collectors, {name: 'Bitbucket'}).id,
					options: {
						scm: 'Bitbucket',
						url: ctrl.repoUrl,
						branch: ctrl.gitBranch,
						userID: ctrl.repouser,
						password: ctrl.repopass
					}
				};
			} else if  (ctrl.repoOption.name.indexOf("Subversion") != -1) {
				item = {
					collectorId : _.findWhere(ctrl.collectors, { name: 'Subversion' }).id,
					options: {
						scm: 'Subversion',
						url: ctrl.repoUrl,
						userID: ctrl.repouser,
						password: ctrl.repopass
					}
				};
			} else if (ctrl.repoOption.name.indexOf("Gitlab") != -1) {
				item = {
					collectorId : _.findWhere(ctrl.collectors, { name: 'Gitlab' }).id,
					options: {
						scm: 'Gitlab',
						url: ctrl.repoUrl,
						userID: ctrl.repouser,
						password: ctrl.repopass
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
					branch : ctrl.gitBranch,
					userID : ctrl.repouser,
					password : ctrl.repopass
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};

			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
	}
})();
