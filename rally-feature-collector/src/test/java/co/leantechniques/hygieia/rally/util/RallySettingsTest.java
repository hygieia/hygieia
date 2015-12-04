package co.leantechniques.hygieia.rally.util;

import co.leantechniques.hygieia.rally.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource("/rally-feature-collector.properties")
public class RallySettingsTest {

    @Autowired
    RallySettings rallySettings;

    @Test
    public void rallySettinsAreLoadedFromPropertiesFile() throws Exception {
        assertThat(rallySettings.getCron(), is("0 * * * * *"));
    }
}