package zoz.cool.javis.common.util.json_util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class StringToJsonNodeSerializer extends JsonSerializer<String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        JsonNode node = mapper.readTree(value);
        gen.writeObject(node);
    }
}
