(function () {
    'use strict';

    angular
    .module(HygieiaConfig.module).controller('CaapmViewController', CaapmViewController);
    CaapmViewController.$inject = ['$q','$scope', 'caapmData', '$uibModal'];

    function CaapmViewController($q,$scope, caapmData, $uibModal) {
    	var ctrl = this;
    	
    	ctrl.noDataAlers = [];
    	ctrl.normalAlerts =[];
    	ctrl.dangertAlerts = [];
    	ctrl.cautionAlerts = [];
    	ctrl.totalAlerts = [];
    	ctrl.worstAlerts = [];
    	
    	 ctrl.totalAlertsCount =0;
		 ctrl.noDataAlersCount =0;
	     ctrl.normalAlertsCount =0;
	     ctrl.dangertAlertsCount =0;
	     ctrl.cautionAlertsCount = 0;
	     var worstCount = 0;
    	 ctrl.load = function() {
    		 caapmData.details($scope.widgetConfig.options.name).then(processResponse);
    	 }
    	 
    	 function processResponse(data) {    		 
    		 ctrl.totalAlertsCount = data.length;
    		 ctrl.noDataAlersCount = countNoDataAlerts();
    	     ctrl.normalAlertsCount = countNormalAlerts();
    	     ctrl.dangertAlertsCount = countDangerAlerts();
    	     ctrl.cautionAlertsCount = countCautionAlerts();
     	    
    		 function countNoDataAlerts() {
                 return _.filter(data, function (alert) {
                     return alert.alertCurrStatus == 0;
                 }).length;
             }
    		 
    		 function countNormalAlerts() {
                 return _.filter(data, function (alert) {
                     return (alert.alertCurrStatus == 1 || alert.alertCurrStatus == 0);
                 }).length;
             }
    		 
    		 function countCautionAlerts() {
                 return _.filter(data, function (alert) {
                     return alert.alertCurrStatus == 2;
                 }).length;
             }
    		 function countDangerAlerts() {
                 return _.filter(data, function (alert) {
                     return alert.alertCurrStatus == 3;
                 }).length;
             }
    		 
    		 ctrl.totalAlerts = data;
    		 
    		 ctrl.normalAlerts = _.where(data, function (alert) {
                  return alert.alertCurrStatus == 1;
              });
    		 
    		 ctrl.noDataAlers = _.where(data, function (alert) {
                 return alert.alertCurrStatus == 0;
             });
    	     ctrl.dangertAlerts =  _.where(data, function (alert) {
                 return alert.alertCurrStatus == 3;
             });
    	     ctrl.cautionAlerts =  _.where(data, function (alert) {
                 return alert.alertCurrStatus ==2;
             });
    	    
    	    
    	    var worstCount = 0;
    	    var worstAlerts = [];
    	    angular.forEach(ctrl.dangertAlerts, function(value, key) {
    	    	worstCount++;
    	    	if(worstCount <= 10){
    	    		value.count = worstCount;
    	    		worstAlerts.push(value);
    	    	}
    	    });
    	    if(worstCount <= 10){
    	    angular.forEach(ctrl.cautionAlerts, function(value, key) {
    	    	worstCount++;    	    	
    	    	if(worstCount <= 10){
    	    		value.count = worstCount;
    	    		worstAlerts.push(value);
    	    	}
    	    });
    	    
    	    
    	    angular.forEach(ctrl.normalAlerts, function(value, key) {
    	    	worstCount++;    	    	
    	    	if(worstCount <= 10){
    	    		value.count = worstCount;
    	    		worstAlerts.push(value);
    	    	}
    	    });
    	   }
    	    ctrl.worstAlerts = worstAlerts;
    	 }
    }
})();