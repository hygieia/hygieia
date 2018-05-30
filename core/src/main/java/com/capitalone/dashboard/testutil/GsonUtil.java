package com.capitalone.dashboard.testutil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class GsonUtil {
    private static final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(ObjectId.class, (JsonSerializer<ObjectId>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toHexString()))
            .registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) GsonUtil::deserialize);

    private static ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json instanceof JsonObject) {
            JsonObject jo = (JsonObject) json;
            return new ObjectId(jo.get("timestamp").getAsInt(), jo.get("machineIdentifier").getAsInt(), jo.get("processIdentifier").getAsShort(), jo.get("counter").getAsInt());
        }
        return new ObjectId(json.getAsString());
    }


    public static Gson getGson() {
        return gsonBuilder.create();
    }
}

