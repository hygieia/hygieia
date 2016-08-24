/**
 * Performance widget configuration
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
        var component = modalData.dashboard.application.components[0];
        ctrl.paToolsDropdownPlaceholder = 'Loading Performance Analysis Jobs...';

        ctrl.submit = submitForm;
        collectorData.itemsByType('appPerformance').then(processPaResponse);

        function processPaResponse(data) {
            var paCollectorItems = component.collectorItems.AppPerformance;
            var paCollectorItemId = _.isEmpty(paCollectorItems) ? null : paCollectorItems[0].id;
            ctrl.paJobs = data;
            ctrl.paCollectorItem = paCollectorItemId ? _.findWhere(ctrl.paJobs, {id: paCollectorItemId}) : null;
            ctrl.paToolsDropdownPlaceholder = data.length ? 'Select a Performance Analysis Job' : 'No Performance Analysis Job Found';
        }

		// public variables
		ctrl.submitted = false;


        function submitForm(paCollectorItem) {
            var collectorItems = [];
            console.log(paCollectorItem);
            if (paCollectorItem) collectorItems.push(paCollectorItem.id);
            var postObj = {
                name: 'performanceanalysis',
                options: {
                    id: widgetConfig.options.id
                },
                componentId: component.id,
                collectorItemIds: collectorItems
            };
            // pass this new config to the modal closing so it's saved
            $modalInstance.close(postObj);
        }


	}
})();
