/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('RepoConfigController',
			RepoConfigController);

	RepoConfigController.$inject = [ 'modalData', '$uibModalInstance',
			'collectorData' ];
	function RepoConfigController(modalData, $uibModalInstance, collectorData) {
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		// Request collectors
		collectorData.collectorsByType('scm').then(processCollectorsResponse);

		function processCollectorsResponse(data) {
			ctrl.collectors = data;

			ctrl.repoOptions =[];
			_(data).forEach(function (collector) {
				ctrl.repoOptions.push({name:collector.name,value:collector.name});
			});
			var collector = modalData.dashboard.application.components[0].collectorItems.SCM;
			var collectorId = (collector!=null && collector[0].collector!=null)? collector[0].collector.id: null;
			if(collectorId!=null){
                collectorData.collectorsById(collectorId).then(function(response){
                    var collectorResponse = response[0];
                    var scmType = 	collectorResponse.name;
                    var myIndex;
                    if(scmType!=null){
                        for (var v = 0; v < ctrl.repoOptions.length; v++) {
                            if (ctrl.repoOptions[v].name.toUpperCase() === scmType.toUpperCase()) {
                                myIndex = v;
                            }
                        }
                        ctrl.repoOption=ctrl.repoOptions[myIndex];
                    }

                });
			}
			// sort collectorItems by lastUpdated
            var sorted = _.sortBy(collector,function(collectorItem){
                return - (new Date(collectorItem.lastUpdated).getTime());
            });

			if(!angular.isUndefined(sorted) && !angular.isUndefined(sorted[0])){
                ctrl.repoUrl = removeGit(sorted[0].options.url);
                ctrl.gitBranch = sorted[0].options.branch;
                ctrl.repouser = sorted[0].options.userID;
                ctrl.repopass = sorted[0].options.password;
                ctrl.repoPersonalAccessToken = sorted[0].options.personalAccessToken;
			}


		}

		// public variables
		ctrl.submitted = false;
		ctrl.collectors = [];

		// public methods
		ctrl.submit = submitForm;



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
						$uibModalInstance.close();
						return;
					}
				}

				if (ctrl.repopass) {
					if (ctrl.repopass === widgetConfig.options.password) {
						//password is unchanged in the form so don't encrypt it again
						try {
							createCollectorItem().then(processCollectorItemResponse, handleError);
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
								createCollectorItem().then(processCollectorItemResponse, handleError);
							} catch (e) {
								console.log(e);
							}
						});
					}
				}
				if (ctrl.repoPersonalAccessToken) {
					if (ctrl.repoPersonalAccessToken === widgetConfig.options.personalAccessToken) {
						//password is unchanged in the form so don't encrypt it again
						try {
							createCollectorItem().then(processCollectorItemResponse, handleError);
						} catch (e) {
							console.log(e);
						}
					} else {
						collectorData.encrypt(ctrl.repoPersonalAccessToken).then(function (response) {

							ctrl.repoPersonalAccessToken = response;
							try {
								createCollectorItem().then(processCollectorItemResponse, handleError);
							} catch (e) {
								console.log(e);
							}
						});
					}
				}
				else {
					createCollectorItem().then(processCollectorItemResponse, handleError);
				}
			}
		}

		/*
		 * function createCollectorItem(url) { var item = { // TODO - Remove
		 * hard-coded subversion reference when mulitple // scm collectors
		 * become available collectorId : _.find(ctrl.collectors, { name :
		 * 'Subversion' }).id, options : { url : url } }; return
		 * collectorData.createCollectorItem(item); }
		 */

		function getNonNullString(value) {
			return _.isEmpty(value)||_.isUndefined(value)?"":value
		}

		function removeGit(url){
			if (!angular.isUndefined(url) && url.endsWith(".git")) {
				url = url.substring(0, url.lastIndexOf(".git"));
			}
			return url;
		}
		function getOptions(scm) {
			return {
				url: removeGit(ctrl.repoUrl),
				branch: getNonNullString(ctrl.gitBranch),
                userID: getNonNullString(ctrl.repouser),
                password: getNonNullString(ctrl.repopass),
				personalAccessToken:getNonNullString(ctrl.repoPersonalAccessToken)
			}
		}

		function getUniqueOptions (scm) {
			return {
                url: removeGit(ctrl.repoUrl),
                branch: ctrl.gitBranch
			}
		}

		function createCollectorItem() {
			var item = {};

			if (ctrl.repoOption.name.indexOf("GitHub") !== -1) {

				item = {
					collectorId: _.find(ctrl.collectors, {name: 'GitHub'}).id,
					options: getOptions('Github'),
					uniqueOptions: getUniqueOptions('Github')
				};
			} else if (ctrl.repoOption.name.indexOf("Bitbucket") !== -1) {

				item = {
					collectorId: _.find(ctrl.collectors, {name: 'Bitbucket'}).id,
					options: getOptions('Bitbucket'),
                    uniqueOptions: getUniqueOptions('Bitbucket')
				};
			} else if  (ctrl.repoOption.name.indexOf("Subversion") !== -1) {
				item = {
					collectorId : _.find(ctrl.collectors, { name: 'Subversion' }).id,
                    options: getOptions('Subversion'),
                    uniqueOptions: getUniqueOptions('Subversion')
				};
			} else if (ctrl.repoOption.name.indexOf("Gitlab") !== -1) {
				item = {
					collectorId : _.find(ctrl.collectors, { name: 'Gitlab' }).id,
                    options: getOptions('Gitlab'),
                    uniqueOptions: getUniqueOptions('Gitlab')
				};
			}
			return collectorData.createCollectorItem(item);
		}

		function handleError(response) {
			if(response.status === 401) {
				$modalInstance.close();
			}
		}

		function processCollectorItemResponse(response) {
			var postObj = {
				name : "repo",
				options:{
					id: widgetConfig.options.id
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};
			// pass this new config to the modal closing so it's saved
			$uibModalInstance.close(postObj);
		}
	}
})();
