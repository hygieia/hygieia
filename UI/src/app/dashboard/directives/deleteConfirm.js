/**
 * Standard delete directive for various components
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('deleteConfirm', function () {
            return {
            	restrict: 'A',
            	scope:true,
                replace: false,
                transclude: false,                            
                link: function(scope,element, attrs, ctrl) {
                	element.bind('click',function(){                		
                		swal({
                      	   title: "Are you sure you want to delete?",
                           showCancelButton: true,
                      	   confirmButtonColor: "#DD6B55",confirmButtonText: "Delete",
                      	   cancelButtonText: "Cancel",                      	   
                      	   closeOnConfirm: true,
                      	   closeOnCancel: true }, 
                      	   function(isConfirm){ 
                      	    if (isConfirm) {
                      		 scope.$apply(attrs.confirmAction);
                      		                      	   } 
                      	});
                	});
               	}
               };
        });
})();