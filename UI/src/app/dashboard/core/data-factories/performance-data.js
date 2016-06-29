/**
 * Gets code repo related data
 */

(function(){
	'use strict';

	angular
		.module(HygieiaConfig.module + '.core')
		.factory('performanceData', performanceData);

	function performanceData($http){
		var caReportRoute = 'api/ad_report.json';
		var testReportRoute = 'test-data/ad_report.json';

		return {
			report: report
		};

		function report(params){ //switch the order of the routes when done with api calls!!!!!
			console.log("I'm here!");
			return $http.get(/*HygieiaConfig.local ? testReportRoute : */caReportRoute /*,{params: params}*/)
					.then(function(response) {
						console.log("hit something");
						return response.data;
					});
			}
	}
})();
