package hygieia.transformer;

import com.capitalone.dashboard.model.quality.QualityVisitee;
import com.capitalone.dashboard.model.quality.CucumberJsonReport;
import com.capitalone.dashboard.model.quality.MochaJsSpecReport;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created by stevegal on 2019-03-25.
 */
public class QualityVisiteeDeserializer extends JsonDeserializer<QualityVisitee> {
    @Override
    public QualityVisitee deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // this is a simple test, but we've only got 2 to distinguish between here, so this test will suffice
        if (JsonToken.START_ARRAY == jsonParser.currentToken()) {
            return jsonParser.getCodec().readValue(jsonParser, CucumberJsonReport.class);
        }
        return jsonParser.getCodec().readValue(jsonParser, MochaJsSpecReport.class);
    }
}
