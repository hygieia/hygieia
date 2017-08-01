package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.MaturityModel;
import com.capitalone.dashboard.repository.MaturityModelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MaturityModelServiceImplTest {

    @Mock
    private MaturityModelRepository maturityModelRepository;

    @InjectMocks
    private MaturityModelServiceImpl service;

    @Test
    public void getProfileAndRules() {
        MaturityModel maturityModel = new MaturityModel();
        maturityModel.setProfile("test");
        maturityModel.setRules("{\n" +
                "  \"profile\": \"test\",\n" +
                "  \"rules\": [{\n" +
                "      \"exhibitAs\": \"Version Control\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"dashboardData\",\n" +
                "        \"method\": \"detail\",\n" +
                "        \"args\": [{\n" +
                "          \"name\": \"$dashboardid\"\n" +
                "\n" +
                "        }],\n" +
                "        \"result\": {\n" +
                "          \"location\": \"application.components.0.collectorItems.SCM.0\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"rules\": {\n" +
                "        \"==\": [{\n" +
                "          \"var\": \"enabled\"\n" +
                "        }, true]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Branching Strategy\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"branchesData\",\n" +
                "        \"method\": \"details\",\n" +
                "        \"args\": [{\n" +
                "          \"name\": \"$collectoritemid\"\n" +
                "        }],\n" +
                "        \"result\": {\n" +
                "          \"location\": \"options.branches.length\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"rules\": {\n" +
                "        \"<\": [{\n" +
                "          \"var\": \"compare\"\n" +
                "        }, 4]\n" +
                "      }\n" +
                "    },\n" +
                "\n" +
                "\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Code Coverage\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"codeAnalysisData\",\n" +
                "        \"method\": \"staticDetails\",\n" +
                "        \"args\": [{\n" +
                "          \"name\": \"params\",\n" +
                "          \"value\": {\n" +
                "            \"max\": 1,\n" +
                "            \"componentId\": \"$componentid\"\n" +
                "          }\n" +
                "        }],\n" +
                "        \"result\": {\n" +
                "          \"location\": \"result.0.metrics\",\n" +
                "          \"rule\": [{\n" +
                "            \"==\": [{\n" +
                "              \"var\": \"name\"\n" +
                "            }, \"line_coverage\"]\n" +
                "          }]\n" +
                "        }\n" +
                "      },\n" +
                "      \"rules\": {\n" +
                "        \">\": [{\n" +
                "          \"var\": \"value\"\n" +
                "        }, 80]\n" +
                "      }\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Automated Build\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"buildData\",\n" +
                "        \"method\": \"details\",\n" +
                "        \"args\": [{\n" +
                "          \"name\": \"params\",\n" +
                "          \"value\": {\n" +
                "            \"componentId\": \"$componentid\"\n" +
                "          }\n" +
                "        }],\n" +
                "        \"result\": {\n" +
                "          \"location\": \"result\",\n" +
                "          \"rule\": [\"$length-1\"]\n" +
                "\n" +
                "\n" +
                "        }\n" +
                "      },\n" +
                "      \"rules\": [{\n" +
                "        \"==\": [{\n" +
                "          \"var\": \"buildStatus\"\n" +
                "        }, \"Success\"]\n" +
                "      }]\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Static Analysis\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"codeAnalysisData\",\n" +
                "        \"method\": \"staticDetails\",\n" +
                "        \"args\": [{\n" +
                "          \"name\": \"params\",\n" +
                "          \"value\": {\n" +
                "            \"max\": 1,\n" +
                "            \"componentId\": \"$componentid\"\n" +
                "          }\n" +
                "        }],\n" +
                "        \"result\": {\n" +
                "          \"location\": \"result.0.metrics\",\n" +
                "          \"rule\": [{\n" +
                "            \"==\": [{\n" +
                "              \"var\": \"name\"\n" +
                "            }, \"major_violations\"]\n" +
                "          }, {\n" +
                "            \"==\": [{\n" +
                "              \"var\": \"name\"\n" +
                "            }, \"critical_violations\"]\n" +
                "          }],\n" +
                "          \"find\": \"formattedValue\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"rules\": {\n" +
                "        \"and\": [{\n" +
                "            \"<=\": [{\n" +
                "              \"var\": \"major_violations\"\n" +
                "            }, 50]\n" +
                "          },\n" +
                "          {\n" +
                "            \"<\": [{\n" +
                "              \"var\": \"critical_violations\"\n" +
                "            }, 1]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Security Scan\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Open Source Scan\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Auto Provision\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Immutable Servers\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Artifact Mangement\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Automated Integration\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Automated Performannce\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Automated Rolback\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Auto Gen of COs\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Zero Down time Prod Release\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"exhibitAs\": \"Feature Toggle\",\n" +
                "      \"source\": {\n" +
                "        \"api\": \"NA\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        when(maturityModelRepository.findByProfile("test")).thenReturn(maturityModel);
        MaturityModel actual = service.getMaturityModel("test");
        assertNotNull(actual);
        assertEquals(actual.getProfile(),"test");
    }

    @Test
    public void getAllProfiles() {
        when(maturityModelRepository.getAllProfiles()).thenReturn(new ArrayList<String>());
        assertNotNull(service.getProfiles());
    }

}
