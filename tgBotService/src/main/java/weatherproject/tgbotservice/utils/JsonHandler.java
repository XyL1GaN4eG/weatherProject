package weatherproject.tgbotservice.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class JsonHandler {

    public static String toJson(Object object) {
        if (object instanceof List<?>) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll((List<?>) object);
            return jsonArray.toJSONString();
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll((JSONObject) object);
            return jsonObject.toJSONString();
        }
    }

    public static <T> T toObject(Object object, Class<T> clazz) {
        try {
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                return clazz.cast(jsonObject);
            } else {
                log.error("Object is not an instance of JSONObject");
                return null;
            }
        } catch (ClassCastException e) {
            log.error("Class cast exception: {}", e.getMessage());
            return null;
        }
    }

    public static List<String> toList(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            List<String> list = new ArrayList<>();
            for (Object obj : jsonArray) {
                list.add((String) obj);
            }
            return list;
        } catch (ParseException e) {
            log.error("JSON parsing exception: {}", e.getMessage());
            return List.of();
        }
    }
}
