/**
 * Displays a duration in human readable format
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .filter('duration', durationFilter);

    function durationFilter() {
    	return function(input) {
    		var duration = moment.duration(input);
    		var output = '';
    		
    		if (duration.hours()) {
    			output = duration.hours() + 'h ';
    		}
    		if (duration.minutes()) {
    			output += duration.minutes() + 'm ';
    		}
    		if (duration.seconds()) {
    			output += duration.seconds() + 's ';
    		}
    		if (!output) {
    			output = input + 'ms';
    		}
    		return output.trim();
    	};
    }
})();