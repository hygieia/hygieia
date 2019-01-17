package com.capitalone.dashboard.bitbucketapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BitbucketUrlParserTest {

    @Test
    public void parseBitbucketUrl() {

        BitbucketUrlParser.BitbucketUrlParts bitbucketUrlParts = BitbucketUrlParser.parseBitbucketUrl("https://mycompany.com/XYZ/rsa");

        assertNotNull(bitbucketUrlParts.getHost());
        assertEquals(bitbucketUrlParts.getHost(),"mycompany.com");

        assertNotNull(bitbucketUrlParts.getProjectKey());
        assertEquals(bitbucketUrlParts.getProjectKey(),"XYZ");

        assertNotNull(bitbucketUrlParts.getRepoKey());
        assertEquals(bitbucketUrlParts.getRepoKey(),"rsa");

        assertNotNull(bitbucketUrlParts.getScheme());
        assertEquals(bitbucketUrlParts.getScheme(),"https");
    }
}

/*
 * Copyright 2019 Pandora Media, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See accompanying LICENSE file or you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
