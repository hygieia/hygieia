package endpoints;

import org.junit.runner.RunWith;

import com.intuit.karate.junit4.Karate;

import cucumber.api.CucumberOptions;

@RunWith(Karate.class)
@CucumberOptions(plugin = {"pretty", "json:target/cucumber/cucumber.json", "html:target/cucumber"}, tags = {"~@ignore"})
public class ApiRunnerTest {

    /**
     * This class act as a test runner for the feature files located at this
     * level and any child directories. This class can also be run as a
     * parameter from the mvn commandline;
     *
     * mvn test -DargLine="-Dkarate.env=dev" -Dtest=ApiRunner
     *
     */

}