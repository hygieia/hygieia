/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CapOneTemplateController', CapOneTemplateController);

    CapOneTemplateController.$inject = ['$interval'];
    function CapOneTemplateController($interval) {
        var ctrl = this;

        ctrl.tabs = [
            { name: "Widget"},
            { name: "Pipeline"},
            { name: "Cloud"}
        ];


        ctrl.minitabs = [
            { name: "Quality"},
            { name: "Performance"}

        ];

        ctrl.miniFeaturetabs = [
            { name: "Feature"},
            { name: "Team"}

        ];

        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        ctrl.miniWidgetView = ctrl.minitabs[0].name;
        ctrl.miniToggleView = function (index) {
            ctrl.miniWidgetView = typeof ctrl.minitabs[index] === 'undefined' ? ctrl.minitabs[0].name : ctrl.minitabs[index].name;
        };

        ctrl.miniFeatureWidgetView = ctrl.miniFeaturetabs[0].name;
        ctrl.miniFeatureToggleView = function (index) {
            ctrl.miniFeatureWidgetView = typeof ctrl.miniFeaturetabs[index] === 'undefined' ? ctrl.miniFeaturetabs[0].name : ctrl.miniFeaturetabs[index].name;
        };

        /** Auto Cycle variables */
        var timeoutPromise = null;
        ctrl.changeDetect = null;
        ctrl.pauseCodeAnalysisView = pauseCodeAnalysisView;
        ctrl.animateCodeAnalysisView = animateCodeAnalysisView;
        ctrl.pausePlaySymbol = "pause";

        /**
         * Changes timeout boolean based on agile iterations available,
         * turning off the agile view switching if only one or none are
         * available
         */
        ctrl.startTimeout = function () {
            ctrl.stopTimeout();

            timeoutPromise = $interval(function () {
                animateCodeAnalysisView(false);
            }, 28000);
        }

        /**
         * Stops the current agile iteration cycler promise
         */
        ctrl.stopTimeout = function () {
            $interval.cancel(timeoutPromise);
        };

        /**
         * Starts timeout cycle function by default
         */
        ctrl.startTimeout();

        /**
         * Animates quality view switching
         */
        function animateCodeAnalysisView(resetTimer) {
            // update the selected view
            var currentIndex = ctrl.minitabs.findIndex(x => x.name==ctrl.miniWidgetView);
            var newIndex = currentIndex + 1;

            if (newIndex >= ctrl.minitabs.length){
                ctrl.miniWidgetView = ctrl.minitabs[0].name;
            } else {
                ctrl.miniWidgetView = ctrl.minitabs[newIndex].name;
            }

            if (resetTimer && timeoutPromise.$$state.value != "canceled") {
                ctrl.stopTimeout();
                ctrl.startTimeout();
            }
        }

        /**
         * Pauses quality view switching via manual button from user interaction
         */
        function pauseCodeAnalysisView() {
            if (timeoutPromise.$$state.value === "canceled") {
                ctrl.pausePlaySymbol = "pause";
                ctrl.startTimeout();
            } else {
                ctrl.pausePlaySymbol = "play";
                ctrl.stopTimeout();
            }
        };

    }
})();
