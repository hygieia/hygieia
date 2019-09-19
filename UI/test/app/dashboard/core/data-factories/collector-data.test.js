describe('collectorData', function () {
    var fixedDashboard = {
        template: "widgets",
        application: {
            name: "myapp",
            components: [
                {
                    id: "componet1",
                    name: "application",
                    collectorItems: {
                        SCM: [
                            {
                                id: "collectorItem1",
                                enabled: true,
                                collectorId: "collector1",
                                collector: {
                                    id: "collector1",
                                    name: "GitHub",
                                    collectorType: "SCM",

                                }
                            },
                            {
                                id: "collectorItem2",
                                enabled: true,
                                collectorId: "collector2",
                                collector: {
                                    id: "collector2",
                                    name: "GitHub",
                                    collectorType: "SCM",

                                }
                            }
                        ]
                    }
                }
            ]
        },
        configurationItemBusServName: "serviceName",
        configurationItemBusAppName: "AppName",
        scoreEnabled: false,
        id: "id",
        owner: "Steve",
        title: "Dashboard01",
        activeWidgets: [
            {type: "build", title: "build01"},
            {type: "build", title: "build02"},
            {type: "code", title: "code05"}
        ],
        widgets: [
            {id: "01", name: "repo01", type: "scm", collectorItemIds: ["collectorItem2"]},
            {id: "02", name: "repo02", type: "scm"}
        ]
    };

    beforeEach(
        module(HygieiaConfig.module + '.core')
    );

    it('can get an instance of collector-data', inject(function (collectorData) {
        expect(collectorData).toBeDefined();
    }));


    it('gets the collector from a collector item',inject(function (collectorData) {
       var collectorId = collectorData.findCollectorForWidget(fixedDashboard.application.components[0].collectorItems.SCM,fixedDashboard.widgets[0]);
       expect(collectorId).toBe("collector2")
    }));

    it('handle no config',inject(function (collectorData) {
        var collectorId = collectorData.findCollectorForWidget(fixedDashboard.application.components[0].collectorItems.SCM,null);
        expect(collectorId).toBeFalsy()
    }));

    it('handle config not setup yet',inject(function (collectorData) {
        var collectorId = collectorData.findCollectorForWidget(fixedDashboard.application.components[0].collectorItems.SCM,fixedDashboard.widgets[1]);
        expect(collectorId).toBeFalsy()
    }));
});
