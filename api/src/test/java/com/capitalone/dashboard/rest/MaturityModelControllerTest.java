package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.MaturityModel;
import com.capitalone.dashboard.service.MaturityModelService;
import com.capitalone.dashboard.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebMVCConfig.class})
@WebAppConfiguration
public class MaturityModelControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private MaturityModelService maturityModelService;
    private static final String JSON_RULES = "{\n" +
            "\" +\n" +
            "                \"  \\\"profile\\\": \\\"test\\\",\\n\" +\n" +
            "                \"  \\\"rules\\\": [{\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Version Control\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"dashboardData\\\",\\n\" +\n" +
            "                \"        \\\"method\\\": \\\"detail\\\",\\n\" +\n" +
            "                \"        \\\"args\\\": [{\\n\" +\n" +
            "                \"          \\\"name\\\": \\\"$dashboardid\\\"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"        }],\\n\" +\n" +
            "                \"        \\\"result\\\": {\\n\" +\n" +
            "                \"          \\\"location\\\": \\\"application.components.0.collectorItems.SCM.0\\\"\\n\" +\n" +
            "                \"        }\\n\" +\n" +
            "                \"      },\\n\" +\n" +
            "                \"      \\\"rules\\\": {\\n\" +\n" +
            "                \"        \\\"==\\\": [{\\n\" +\n" +
            "                \"          \\\"var\\\": \\\"enabled\\\"\\n\" +\n" +
            "                \"        }, true]\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Branching Strategy\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"branchesData\\\",\\n\" +\n" +
            "                \"        \\\"method\\\": \\\"details\\\",\\n\" +\n" +
            "                \"        \\\"args\\\": [{\\n\" +\n" +
            "                \"          \\\"name\\\": \\\"$collectoritemid\\\"\\n\" +\n" +
            "                \"        }],\\n\" +\n" +
            "                \"        \\\"result\\\": {\\n\" +\n" +
            "                \"          \\\"location\\\": \\\"options.branches.length\\\"\\n\" +\n" +
            "                \"        }\\n\" +\n" +
            "                \"      },\\n\" +\n" +
            "                \"      \\\"rules\\\": {\\n\" +\n" +
            "                \"        \\\"<\\\": [{\\n\" +\n" +
            "                \"          \\\"var\\\": \\\"compare\\\"\\n\" +\n" +
            "                \"        }, 4]\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Code Coverage\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"codeAnalysisData\\\",\\n\" +\n" +
            "                \"        \\\"method\\\": \\\"staticDetails\\\",\\n\" +\n" +
            "                \"        \\\"args\\\": [{\\n\" +\n" +
            "                \"          \\\"name\\\": \\\"params\\\",\\n\" +\n" +
            "                \"          \\\"value\\\": {\\n\" +\n" +
            "                \"            \\\"max\\\": 1,\\n\" +\n" +
            "                \"            \\\"componentId\\\": \\\"$componentid\\\"\\n\" +\n" +
            "                \"          }\\n\" +\n" +
            "                \"        }],\\n\" +\n" +
            "                \"        \\\"result\\\": {\\n\" +\n" +
            "                \"          \\\"location\\\": \\\"result.0.metrics\\\",\\n\" +\n" +
            "                \"          \\\"rule\\\": [{\\n\" +\n" +
            "                \"            \\\"==\\\": [{\\n\" +\n" +
            "                \"              \\\"var\\\": \\\"name\\\"\\n\" +\n" +
            "                \"            }, \\\"line_coverage\\\"]\\n\" +\n" +
            "                \"          }]\\n\" +\n" +
            "                \"        }\\n\" +\n" +
            "                \"      },\\n\" +\n" +
            "                \"      \\\"rules\\\": {\\n\" +\n" +
            "                \"        \\\">\\\": [{\\n\" +\n" +
            "                \"          \\\"var\\\": \\\"value\\\"\\n\" +\n" +
            "                \"        }, 80]\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Automated Build\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"buildData\\\",\\n\" +\n" +
            "                \"        \\\"method\\\": \\\"details\\\",\\n\" +\n" +
            "                \"        \\\"args\\\": [{\\n\" +\n" +
            "                \"          \\\"name\\\": \\\"params\\\",\\n\" +\n" +
            "                \"          \\\"value\\\": {\\n\" +\n" +
            "                \"            \\\"componentId\\\": \\\"$componentid\\\"\\n\" +\n" +
            "                \"          }\\n\" +\n" +
            "                \"        }],\\n\" +\n" +
            "                \"        \\\"result\\\": {\\n\" +\n" +
            "                \"          \\\"location\\\": \\\"result\\\",\\n\" +\n" +
            "                \"          \\\"rule\\\": [\\\"$length-1\\\"]\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"        }\\n\" +\n" +
            "                \"      },\\n\" +\n" +
            "                \"      \\\"rules\\\": [{\\n\" +\n" +
            "                \"        \\\"==\\\": [{\\n\" +\n" +
            "                \"          \\\"var\\\": \\\"buildStatus\\\"\\n\" +\n" +
            "                \"        }, \\\"Success\\\"]\\n\" +\n" +
            "                \"      }]\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Static Analysis\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"codeAnalysisData\\\",\\n\" +\n" +
            "                \"        \\\"method\\\": \\\"staticDetails\\\",\\n\" +\n" +
            "                \"        \\\"args\\\": [{\\n\" +\n" +
            "                \"          \\\"name\\\": \\\"params\\\",\\n\" +\n" +
            "                \"          \\\"value\\\": {\\n\" +\n" +
            "                \"            \\\"max\\\": 1,\\n\" +\n" +
            "                \"            \\\"componentId\\\": \\\"$componentid\\\"\\n\" +\n" +
            "                \"          }\\n\" +\n" +
            "                \"        }],\\n\" +\n" +
            "                \"        \\\"result\\\": {\\n\" +\n" +
            "                \"          \\\"location\\\": \\\"result.0.metrics\\\",\\n\" +\n" +
            "                \"          \\\"rule\\\": [{\\n\" +\n" +
            "                \"            \\\"==\\\": [{\\n\" +\n" +
            "                \"              \\\"var\\\": \\\"name\\\"\\n\" +\n" +
            "                \"            }, \\\"major_violations\\\"]\\n\" +\n" +
            "                \"          }, {\\n\" +\n" +
            "                \"            \\\"==\\\": [{\\n\" +\n" +
            "                \"              \\\"var\\\": \\\"name\\\"\\n\" +\n" +
            "                \"            }, \\\"critical_violations\\\"]\\n\" +\n" +
            "                \"          }],\\n\" +\n" +
            "                \"          \\\"find\\\": \\\"formattedValue\\\"\\n\" +\n" +
            "                \"        }\\n\" +\n" +
            "                \"      },\\n\" +\n" +
            "                \"      \\\"rules\\\": {\\n\" +\n" +
            "                \"        \\\"and\\\": [{\\n\" +\n" +
            "                \"            \\\"<=\\\": [{\\n\" +\n" +
            "                \"              \\\"var\\\": \\\"major_violations\\\"\\n\" +\n" +
            "                \"            }, 50]\\n\" +\n" +
            "                \"          },\\n\" +\n" +
            "                \"          {\\n\" +\n" +
            "                \"            \\\"<\\\": [{\\n\" +\n" +
            "                \"              \\\"var\\\": \\\"critical_violations\\\"\\n\" +\n" +
            "                \"            }, 1]\\n\" +\n" +
            "                \"          }\\n\" +\n" +
            "                \"        ]\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Security Scan\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Open Source Scan\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Auto Provision\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Immutable Servers\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Artifact Mangement\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Automated Integration\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Automated Performannce\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Automated Rolback\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Auto Gen of COs\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Zero Down time Prod Release\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    },\\n\" +\n" +
            "                \"    {\\n\" +\n" +
            "                \"      \\\"exhibitAs\\\": \\\"Feature Toggle\\\",\\n\" +\n" +
            "                \"      \\\"source\\\": {\\n\" +\n" +
            "                \"        \\\"api\\\": \\\"NA\\\"\\n\" +\n" +
            "                \"      }\\n\" +\n" +
            "                \"    }\\n\" +\n" +
            "                \"  ]\\n\" +\n" +
            "                \"}";

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getProfiles() throws Exception {
        MaturityModel maturityModel = new MaturityModel();
        maturityModel.setProfile("test");
        maturityModel.setRules(JSON_RULES);
        List<String> profiles = new ArrayList<>();
        profiles.add("test");
        when(maturityModelService.getProfiles()).thenReturn(profiles);

        mockMvc.perform(get("/maturityModel/profiles").contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        verify(maturityModelService).getProfiles();

    }

    @Test
    public void getMaturityModel() throws Exception {
        MaturityModel maturityModel = new MaturityModel();
        maturityModel.setProfile("test");
        maturityModel.setRules(JSON_RULES);
        when(maturityModelService.getMaturityModel("test")).thenReturn(maturityModel);

        String profile = "test";
        mockMvc.perform(get("/maturityModel/profile/"+profile).contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).equals(maturityModel);
        verify(maturityModelService).getMaturityModel("test");
   }

}
