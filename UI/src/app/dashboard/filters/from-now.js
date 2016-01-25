/**
 * Displays a date as a human-readable time difference from current time
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .filter('fromNow', fromNowFilter);

    function fromNowFilter() {
    	return function(input) {
    		return input ? moment(input).dash('ago') : '';
    	};
    }
})();