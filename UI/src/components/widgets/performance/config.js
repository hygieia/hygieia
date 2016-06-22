/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('performanceConfigController',
			performanceConfigController);

	performanceConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData' ];
	function performanceConfigController(modalData, $modalInstance, collectorData) {
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;


//console.log(JSON.stringify(widgetConfig)); //"{"options":{"id":"repo0"}}"
//		console.log(JSON.stringify(widgetConfig.options.id));

		ctrl.appId = "kek";

		// public variables
		ctrl.submitted = false;

		// public methods
		ctrl.submit = submitForm;

		// Request collecters
		//collectorData.collectorsByType('scm').then(processCollectorsResponse);

		function processCollectorsResponse(data) {
			ctrl.collectors = data;
		}

		/*
		 * function submitForm(valid, url) { ctrl.submitted = true; if (valid &&
		 * ctrl.collectors.length) {
		 * createCollectorItem(url).then(processCollectorItemResponse); } }
		 */

		function submitForm() {
			ctrl.submitted = true;
		}


	}
})();
