export let API_TOKEN_LIST = [
    {
        id: '12345678901234567890',
        apiUser: 'testing',
        apiKey: 'sha512:f216973c3e3a2e34b2d6595a17254a24c278f2051' +
            +'9340d22248a143d4d5dc6b79399c099898adb8be94598d19c6520c6ea75996498860151de47b64e83c1dc75',
        expirationDt: 1656475199999,
        hashed: true
    },
    {
        id: '12345678901234567890',
        apiUser: 'testing1',
        apiKey: 'sha512:a316973c3e3a2e34b2d6595a17254a24c278' +
            +'f20519340d22248a143d4d5dc6b79399c099898adb8be94598d19c6520c6ea75996498860151de47b64e83c1dc75',
        expirationDt: 1656485199999,
        hashed: true
    }
];

export let USER_LIST = [
    {
        id: '5bf62b6a67c2ba05fcc8baa8',
        username: 'test',
        authorities: ['ROLE_USER'],
        authType: 'LDAP',
        firstName: 'test',
        lastName: 'test',
        emailAddress: 'test@example.com'
    },

    {
        id: '5bf83b6a67c2ba05fcc8baa8',
        username: 'user',
        authorities: ['ROLE_ADMIN'],
        authType: 'LDAP',
        firstName: 'test1',
        lastName: 'test1',
        emailAddress: 'test1@example.com'
    }
];

export let DASHBOARDDATA = [
    [{
        id: '56d9cf6b7fab7c42f5918b84',
        template: 'test',
        title: 'test',
        widgets: [
            {
                id: '56d9d0407fab7c42f5918b88',
                name: 'test',
                componentId: '56d9cf6b7fab7c42f5918b83',
                options:
                {
                    id: 'repo0',
                    scm:
                    {
                        name: 'test',
                        value: 'test'
                    },
                    branch: 'develop',
                    url: 'https://example.com/'
                }
            },
            {
                id: '56d9d2417fab7c42f5918b8c',
                name: 'deploy',
                componentId: '56d9cf6b7fab7c42f5918b83',
                options: {
                    id: 'deploy0'
                }
            },
            {
                id: '56d9d3047fab7c42f5918b8d',
                name: 'test',
                options:
                {
                    mappings:
                    {
                        dev: 'test1',
                        qa: 'test1'
                    },
                    id: 'test1'
                }
            },
            {
                id: '5c39705940b65d70e849b6c9',
                name: 'test',
                componentId: '56d9cf6b7fab7c42f5918b83',
                options: {
                    id: 'test0',
                    featureTool: 'Jira',
                    teamName: 'test',
                    teamId: '14585',
                    projectName: 'test',
                    projectId: '42300',
                    showStatus:
                        { kanban: false, scrum: true },
                    estimateMetricType: 'test',
                    sprintType: 'scrum',
                    listType: 'epics'
                }
            }]
        , owner: 'name',
        owners: [{ username: 'admin', authType: 'STANDARD' }],
        type: 'Team',
        application: {
            name: 'Test1',
            components: [{
                id: '56d9cf6b7fab7c42f5918b83',
                name: 'Test1',
                collectorItems:
                {
                    ScopeOwner: [{
                        id: '56d9ab057fab7c402ac9f730',
                        description: 'test',
                        enabled: true,
                        errors: [],
                        pushed: false,
                        collectorId: '56d825dd7fab7c769f58f246',
                        lastUpdated: 0,
                        options: {
                            isDeleted: 'False',
                            teamId: 'Team:17292934',
                            changeDate: '2015-11-06T12:52:08.2030000',
                            assetState: 'Active'
                        },
                        name: 'test',
                        teamId: 'Team:1729eee2934',
                        changeDate: '2015-11-06T12:52:08.2030000',
                        isDeleted: 'False',
                        assetState: 'Active',
                        errorCount: 0
                    }],
                    SCM: [{
                        id: '56d9d0407fab7c42f5918b87',
                        enabled: true,
                        errors: [],
                        pushed: false,
                        collectorId: '56ca15297fab7c68bfdb420c',
                        lastUpdated: 0,
                        options: {
                            scm: 'Github',
                            branch: 'develop',
                            url: 'https://example.com'
                        }
                        , errorCount: 0
                    }],
                    Deployment: [
                        {
                            id: '56d825c07fab7c76a1453312',
                            description: 'test',
                            enabled: true, errors: [],
                            pushed: false,
                            collectorId: '56d825ba7fab7c76a14530a8',
                            lastUpdated: 0,
                            options: {
                                applicationId: '297e00af-f4cb-4815-9e4a-c26eb3e97de6',
                                applicationName: 'test',
                                instanceUrl: 'https://example.com/'
                            },
                            errorCount: 0
                        }
                    ],
                    AgileTool: [
                        {
                            id: '5c39705940b65d70e849b6c7',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '589febd0a65ce715f0332d62',
                            lastUpdated: 0,
                            options: {
                                teamName: 'test122',
                                featureTool: 'Jira',
                                projectName: 'test1',
                                projectId: '42300',
                                teamId: '14585'
                            },
                            errorCount: 0
                        }
                    ],
                    Audit: [
                        {
                            id: '5cf5ad80c809261e134f7f16',
                            description: 'test12',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf193887dd97b7275fbd2bbae',
                            lastUpdated: 1580895008852,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        },
                        {
                            id: '5cf5ad81c809261e134f7f17',
                            description: 'test122',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388797b7275fbd2bbae',
                            lastUpdated: 1580895009163,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        }, {
                            id: '5cf5ad81c809261e134f7f18',
                            description: 'test123',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388797b7275fbd2bbae',
                            lastUpdated: 1580895009487,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        },
                        {
                            id: '5cf5ad81c809261e134f7f19',
                            description: 'test12',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388797b7275fbd2bbae',
                            lastUpdated: 1580895009801,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        },
                        {
                            id: '5cf5ad81c809261e134f7f1a',
                            description: 'test12',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388797b7275fbd2bbae',
                            lastUpdated: 1580895010123,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        },
                        {
                            id: '5cf5ad81c809261e134f7f1b',
                            description: 'testdashboard',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388797b7275fbd2bbae',
                            lastUpdated: 1580895010467,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        }, {
                            id: '5d28a079f2cf96179ea14c1a',
                            description: 'test12',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19388dd797b7275fbd2bbae',
                            lastUpdated: 1580895010832,
                            options: {
                                dashboardId: '56d9cf6b7fab7ddc42f5918b84'
                            },
                            errorCount: 0
                        },
                        {
                            id: '5d546f56cc6955172824cba9',
                            description: 'test1',
                            enabled: true,
                            errors: [],
                            pushed: false,
                            collectorId: '5bf19ddd388797b7275fbd2bbae',
                            lastUpdated: 1580895008852,
                            options: {
                                dashboardId: '56d9cf6b7fab7c42f5918b84'
                            },
                            errorCount: 0
                        }
                    ]
                }
            }]
        },
        configurationItemBusServName: '45386641',
        configurationItemBusAppName: 'TEST',
        validServiceName: true,
        validAppName: false,
        remoteCreated: false,
        scoreEnabled: true,
        scoreDisplay: 'HEADER',
        createdAt: 0,
        updatedAt: 1588646469622,
        errorCode: 0
    },
    ],
];

export let DASHBOARDDATARESPONSE = [{
    configurationItemBusAppName: 'TEST',
    configurationItemBusServName: '12384541',
    id: '56d9cf6b7fab7c42f5918b84',
    isProduct: false,
    name: 'TEST1',
    scoreDisplay: 'HEADER',
    scoreEnabled: true,
    type: 'Team',
    validAppName: false,
    validServiceName: true,
}];

export let DASHBOARDITEM = {
    configurationItemBusAppName: 'test',
    configurationItemBusServName: 'test',
    id: '56e777b47fab7c2d52bf7049',
    name: 'test-data',
    type: 'Team',
    validServiceName: false,
    validAppName: false,
    isProduct: false,
    scoreEnabled: false,
    scoreDisplay: 'HEADER'
};



export let BUSSERVDATA = [{
    appServiceOwner: 'Test',
    assignmentGroup: 'test',
    businessOwner: 'user',
    collectorItemId: '5be9a3a0e4b0389bb364f5ab',
    commonName: 'test',
    components: ['12397840', '12385100', '1212647767'],
    configurationItem: '34384541',
    configurationItemSubType: 'Service',
    configurationItemType: 'test',
    developmentOwner: 'test ',
    id: '5be9a3a0e4b0389bb364f5ac',
    itemType: 'app',
    ownerDept: 'test',
    ownerSubDept: 'test',
    timestamp: 1580512800701,
    validConfigItem: true,
    }
    ];
