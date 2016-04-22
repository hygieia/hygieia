/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('SubnetVisigothConfigController',
			SubnetVisigothConfigController);

	SubnetVisigothConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData' ];
	function SubnetVisigothConfigController(modalData, $modalInstance, collectorData) {

		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		
	}
})();
