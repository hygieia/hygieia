export let GET_DASHBOARD_MOCK = {
  id: '59f88f5e6a3cf205f312c62f',
  template: 'CapOne',
  title: 'UI',
  widgets: [
    {
      id: '59f88f5f6a3cf205f312c632',
      name: 'repo',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        name: 'repo',
        id: 'repo0',
        type: 'GitHub',
        personalAccessToken: '123',
        username: 'user',
        password: 'pass',
        scm: {
          name: 'GitHub',
          value: 'GitHub'
        },
        branch: 'master',
        url: 'www.github.com'
      }
    },
    {
      id: '5b281a422dbb6e05ecb9b139',
      name: 'deploy',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'deploy0',
        aggregateServers: false,
        ignoreRegex: ''
      }
    },
    {
      id: '5b281c3f2dbb6e05ecb9b13d',
      name: 'pipeline',
      options: {
        mappings: {
          DEV: 'dev',
          QA: 'qa',
          PROD: 'prod'
        },
        id: 'pipeline0',
        prod: 'PROD',
        order: {
          0: 'DEV',
          1: 'QA',
          2: 'PROD'
        }
      }
    },
    {
      id: '5b2aa4232dbb6e05ecb9b55a',
      name: 'feature',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'feature0',
        featureTool: 'Jira',
        teamName: 'My Team',
        teamId: '16910',
        projectName: 'MY ART',
        projectId: '138300',
        showStatus: {
          kanban: true,
          scrum: false
        },
        estimateMetricType: 'storypoints',
        sprintType: 'kanban',
        listType: 'issues'
      }
    },
    {
      id: '5b43aa75be92e005effaa079',
      name: 'codeanalysis',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'codeanalysis0'
      }
    },
    {
      id: '5b4e447994d69005f712dc54',
      name: 'monitor',
      options: {
        id: 'monitor0'
      }
    },
    {
      id: '5bb651b1200a983c7cebeab6',
      name: 'cloud',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'cloud0',
        accountNumber: '913831209578',
        tagName: 'CMDBEnvironment',
        tagValue: 'ENVPRMYENV'
      }
    },
    {
      id: '5d029d59c809261db2bba332',
      name: 'oss',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'oss0'
      }
    },
    {
      id: '5c37a6de40b65d55fde3ddaf',
      name: 'build',
      componentId: '59f88f5e6a3cf205f312c62e',
      options: {
        id: 'build0',
        buildDurationThreshold: 16,
        consecutiveFailureThreshold: 5
      }
    }
  ],
  owners: [
    {
      username: 'artemis',
      authType: 'LDAP'
    },
    {
      username: 'abc123',
      authType: 'LDAP'
    },
    {
      username: 'abc456',
      authType: 'LDAP'
    },
    {
      username: 'abc789',
      authType: 'LDAP'
    }
  ],
  type: 'Team',
  application: {
    name: 'BAP123',
    components: [
      {
        id: '59f88f5e6a3cf205f312c62e',
        name: 'BAP123',
        collectorItems: {
          SCM: [
            {
              id: '59f88f5f6a3cf205f312c631',
              description: 'Registered by Artemis.',
              enabled: true,
              errors: [],
              pushed: false,
              collectorId: '56ca15297fab7c68bfdb420c',
              lastUpdated: 1550247489244,
              options: {
                password: '',
                personalAccessToken: '123',
                branch: 'master',
                userID: '',
                url: 'www.github.com'
              },
              collector: {
                id: '56ca15297fab7c68bfdb420c',
                name: 'GitHub',
                collectorType: 'SCM',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  branch: '',
                  url: ''
                },
                allFields: {
                  password: '',
                  personalAccessToken: '',
                  branch: '',
                  userID: '',
                  url: ''
                },
                lastExecuted: 1555611106792,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 0
            },
            {
              id: '5a147d0d17e34d060d5e19b1',
              description: 'Registered by Artemis.',
              enabled: true,
              errors: [],
              pushed: false,
              collectorId: '56ca15297fab7c68bfdb420c',
              lastUpdated: 1550243720072,
              options: {
                personalAccessToken: '123',
                branch: 'master',
                url: 'www.github.com'
              },
              collector: {
                id: '56ca15297fab7c68bfdb420c',
                name: 'GitHub',
                collectorType: 'SCM',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  branch: '',
                  url: ''
                },
                allFields: {
                  password: '',
                  personalAccessToken: '',
                  branch: '',
                  userID: '',
                  url: ''
                },
                lastExecuted: 1555611106792,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          Deployment: [
            {
              id: '5b2826992dbb6e05ecb9b17b',
              description: 'UI',
              niceName: 'myJenkins',
              enabled: true,
              errors: [],
              pushed: true,
              collectorId: '56f94b617fab7c2d52bf808a',
              lastUpdated: 1543775909189,
              options: {
                applicationName: 'UI',
                instanceUrl: 'https://myJenkins.mycompany.com/'
              },
              collector: {
                id: '56f94b617fab7c2d52bf808a',
                name: 'Jenkins',
                collectorType: 'Deployment',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  applicationName: '',
                  instanceUrl: ''
                },
                allFields: {
                  applicationName: '',
                  instanceUrl: ''
                },
                lastExecuted: 1555426934944,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          Build: [
            {
              id: '5b84328d92678d061457d5f1',
              description: '/CICD_Pipeline/',
              niceName: 'myJenkins',
              enabled: true,
              errors: [],
              pushed: true,
              collectorId: '56ca5b387fab7c05d45a20ce',
              lastUpdated: 1551905024276,
              options: {
                jobName: '/CICD_Pipeline/',
                jobUrl: 'https://myJenkins.mycompany.com/',
                instanceUrl: 'https://myJenkins.mycompany.com/'
              },
              collector: {
                id: '56ca5b387fab7c05d45a20ce',
                name: 'Hudson',
                collectorType: 'Build',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  jobName: '',
                  jobUrl: ''
                },
                allFields: {
                  jobName: '',
                  jobUrl: '',
                  instanceUrl: ''
                },
                lastExecuted: 1555611406295,
                searchFields: [
                  'options.jobName',
                  'niceName'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          AgileTool: [
            {
              id: '5c813f4baa8ebb3c1bf8ba82',
              enabled: true,
              errors: [],
              pushed: false,
              collectorId: '589febd0a65ce715f0332d62',
              lastUpdated: 0,
              options: {
                teamName: 'My Team',
                featureTool: 'Jira',
                projectName: 'MY ART',
                projectId: '138300',
                teamId: '16910'
              },
              collector: {
                id: '589febd0a65ce715f0332d62',
                name: 'Jira',
                collectorType: 'AgileTool',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {},
                allFields: {},
                lastExecuted: 1555610945323,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          CodeQuality: [
            {
              id: '5c639bf1f33a0e3db61d3b39',
              description: 'my-project',
              niceName: 'sonarcloud',
              enabled: true,
              errors: [],
              pushed: false,
              collectorId: '56cb5bde7fab7c548551410e',
              lastUpdated: 1552918302084,
              options: {
                projectName: 'my-project',
                projectId: 'AWYg72bPxyAXI_MLaZwz',
                instanceUrl: 'https://sonar.mycompany.com'
              },
              collector: {
                id: '56cb5bde7fab7c548551410e',
                name: 'Sonar',
                collectorType: 'CodeQuality',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  projectName: '',
                  instanceUrl: ''
                },
                allFields: {
                  projectName: '',
                  projectId: '',
                  instanceUrl: ''
                },
                lastExecuted: 1555611373118,
                searchFields: [
                  'options.projectName',
                  'niceName'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          StaticSecurityScan: [
            {
              id: '5cacc1bad7ab023dab192ec2',
              description: 'my-project',
              enabled: true,
              errors: [],
              pushed: false,
              collectorId: '5991223442ff4e0d3c1485c1',
              lastUpdated: 1554825657461,
              options: {
                reportUrl: 'https://mycompany.com/',
                applicationID: [
                  'ASVMYPROJECT'
                ],
                projectName: 'my-project',
                applicationName: 'my-project',
                instanceUrl: 'https://mycompany.com/'
              },
              collector: {
                id: '5991223442ff4e0d3c1485c1',
                name: 'Eratocode',
                collectorType: 'StaticSecurityScan',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  applicationID: '',
                  projectName: '',
                  applicationName: '',
                  instanceUrl: ''
                },
                allFields: {
                  applicationType: '',
                  reportUrl: '',
                  applicationID: '',
                  projectName: '',
                  applicationName: '',
                  instanceUrl: ''
                },
                lastExecuted: 1555610460244,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 0
            }
          ],
          LibraryPolicy: [
            {
              id: '5c5c97e848f1bf3c30c5b872',
              description: 'my-project',
              enabled: true,
              errors: [
                {
                  errorCode: 'Unreachable',
                  errorMessage: 'No reports found for my-project',
                  timestamp: 1554896968012
                }
              ],
              pushed: false,
              collectorId: '5b10938818a7fe201bf737d1',
              lastUpdated: 1554896968012,
              options: {
                componentName: 'my-project',
                applicationID: [
                  'ASVMYPROJECT'
                ],
                applicationName: 'my-project',
                instanceUrl: 'https://mycompany.com'
              },
              collector: {
                id: '5b10938818a7fe201bf737d1',
                name: 'EratocodeOSS',
                collectorType: 'LibraryPolicy',
                enabled: true,
                online: true,
                errors: [],
                uniqueFields: {
                  componentName: '',
                  instanceUrl: ''
                },
                allFields: {
                  applicationType: '',
                  componentName: '',
                  applicationID: '',
                  applicationName: '',
                  instanceUrl: ''
                },
                lastExecuted: 1555610708767,
                searchFields: [
                  'description'
                ],
                properties: {}
              },
              errorCount: 1
            }
          ]
        }
      }
    ]
  },
  configurationItemBusServName: 'CI384170',
  configurationItemBusAppName: 'CI386307',
  validServiceName: true,
  validAppName: true,
  remoteCreated: false,
  scoreEnabled: false,
  scoreDisplay: 'HEADER',
  activeWidgets: [],
  createdAt: 0,
  updatedAt: 0,
  errorCode: 0
};

export const POST_DASHBOARD_MOCK = {
  component: {
    id: '59f88f5e6a3cf205f312c62e',
    name: 'BAP123',
    collectorItems: {
      SCM: [
        {
          id: '59f88f5f6a3cf205f312c631',
          description: 'Registered by Artemis.',
          enabled: true,
          errors: [],
          pushed: false,
          collectorId: '56ca15297fab7c68bfdb420c',
          lastUpdated: 1550247489244,
          options: {
            password: '',
            personalAccessToken: '123',
            branch: 'master',
            userID: '',
            url: 'www.github.com'
          },
          errorCount: 0
        },
        {
          id: '5a147d0d17e34d060d5e19b1',
          description: 'Registered by Artemis.',
          enabled: true,
          errors: [],
          pushed: false,
          collectorId: '56ca15297fab7c68bfdb420c',
          lastUpdated: 1550243720072,
          options: {
            personalAccessToken: '123',
            branch: 'master',
            url: 'www.github.com'
          },
          errorCount: 0
        }
      ],
      Deployment: [
        {
          id: '5b2826992dbb6e05ecb9b17b',
          description: 'UI',
          niceName: 'myJenkins',
          enabled: true,
          errors: [],
          pushed: true,
          collectorId: '56f94b617fab7c2d52bf808a',
          lastUpdated: 1543775909189,
          options: {
            applicationName: 'UI',
            instanceUrl: 'https://myJenkins.mycompany.com/'
          },
          errorCount: 0
        }
      ],
      AgileTool: [
        {
          id: '5c813f4baa8ebb3c1bf8ba82',
          enabled: true,
          errors: [],
          pushed: false,
          collectorId: '589febd0a65ce715f0332d62',
          lastUpdated: 0,
          options: {
            teamName: 'My Team',
            featureTool: 'Jira',
            projectName: 'MY ART',
            projectId: '138300',
            teamId: '16910'
          },
          errorCount: 0
        }
      ],
      CodeQuality: [
        {
          id: '5c639bf1f33a0e3db61d3b39',
          description: 'my-project',
          niceName: 'sonarcloud',
          enabled: true,
          errors: [],
          pushed: false,
          collectorId: '56cb5bde7fab7c548551410e',
          lastUpdated: 1552918302084,
          options: {
            projectName: 'my-project',
            projectId: 'AWYg72bPxyAXI_MLaZwz',
            instanceUrl: 'https://sonar.mycompany.com'
          },
          errorCount: 0
        }
      ],
      StaticSecurityScan: [
        {
          id: '5cacc1bad7ab023dab192ec2',
          description: 'my-project',
          enabled: true,
          errors: [],
          pushed: false,
          collectorId: '5991223442ff4e0d3c1485c1',
          lastUpdated: 1554825657461,
          options: {
            reportUrl: 'https://mycompany.com/',
            applicationID: [
              'ASVMYPROJECT'
            ],
            projectName: 'my-project',
            applicationName: 'my-project',
            instanceUrl: 'https://mycompany.com/'
          },
          errorCount: 0
        }
      ],
      LibraryPolicy: [
        {
          id: '5c5c97e848f1bf3c30c5b872',
          description: 'my-project',
          enabled: true,
          errors: [
            {
              errorCode: 'Unreachable',
              errorMessage: 'No reports found for my-project',
              timestamp: 1554896968012
            }
          ],
          pushed: false,
          collectorId: '5b10938818a7fe201bf737d1',
          lastUpdated: 1554896968012,
          options: {
            componentName: 'my-project',
            applicationID: [
              'ASVMYPROJECT'
            ],
            applicationName: 'my-project',
            instanceUrl: 'https://mycompany.com'
          },
          errorCount: 1
        }
      ],
      Build: [
        {
          id: '5b84328d92678d061457d5f1',
          description: '/CICD_Pipeline/',
          niceName: 'myJenkins',
          enabled: true,
          errors: [],
          pushed: true,
          collectorId: '56ca5b387fab7c05d45a20ce',
          lastUpdated: 1554932326734,
          options: {
            jobName: '/CICD_Pipeline/',
            jobUrl: 'https://myJenkins.mycompany.com',
            instanceUrl: 'https://myJenkins.mycompany.com/'
          },
          collector: {
            id: '56ca5b387fab7c05d45a20ce',
            name: 'Hudson',
            collectorType: 'Build',
            enabled: true,
            online: true,
            errors: [],
            uniqueFields: {
              jobName: '',
              jobUrl: ''
            },
            allFields: {
              jobName: '',
              jobUrl: '',
              instanceUrl: ''
            },
            lastExecuted: 1554989530787,
            searchFields: [
              'options.jobName',
              'niceName'
            ],
            properties: {}
          },
          errorCount: 0
        }
      ]
    }
  },
  widget: {
    id: '5c37a6de40b65d55fde3ddaf',
    name: 'build',
    componentId: '59f88f5e6a3cf205f312c62e',
    options: {
      id: 'build0',
      buildDurationThreshold: 15,
      consecutiveFailureThreshold: 5
    }
  }
};
