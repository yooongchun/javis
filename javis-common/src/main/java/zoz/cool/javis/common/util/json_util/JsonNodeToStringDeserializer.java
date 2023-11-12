package zoz.cool.javis.common.util.json_util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonNodeToStringDeserializer extends JsonDeserializer<String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = mapper.readTree(p);
        return node.toString();
    }
}