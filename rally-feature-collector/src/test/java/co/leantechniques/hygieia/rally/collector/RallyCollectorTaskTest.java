package co.leantechniques.hygieia.rally.collector;

import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RallyCollectorTaskTest {

    private RallyCollectorTask collector;

    @Before
    public void setUp() throws Exception {
        collector = new RallyCollectorTask(null, null, null, null, null);
    }

    @Test
    public void shouldExtendCollectorTask(){
        assertThat(collector, instanceOf(CollectorTask.class));
    }

    @Test
    public void getCollector() throws Exception {
        Collector actual = this.collector.getCollector();
        assertThat(actual.getCollectorType(), is(CollectorType.Feature));
        assertThat(actual.getName(), is("Rally"));
        assertThat(actual.isOnline(), is(true));
        assertThat(actual.isEnabled(), is(true));
    }
}
