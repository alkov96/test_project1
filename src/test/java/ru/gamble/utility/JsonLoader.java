package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.TestDataObject;
import ru.sbtqa.tag.datajack.adaptors.json.JsonDataObjectAdaptor;
import ru.sbtqa.tag.datajack.exceptions.DataException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class JsonLoader {
    private static TestDataObject data;
    private static final Logger LOG = LoggerFactory.getLogger(JsonLoader.class);
    private static String sep = File.separator;

    public static TestDataObject getData() {
            try {
                data = new JsonDataObjectAdaptor("src" + sep + "test" + sep + "resources", "data");
            } catch (DataException e) {
                LOG.error(e.getMessage());
            }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> flatMap(String parentKey, Map<String, Object> nestedMap)
    {
        Map<String, String> flatMap = new HashMap<>();
        String prefixKey = parentKey != null ? parentKey + "." : "";
        for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                flatMap.put(prefixKey + entry.getKey(), (String)entry.getValue());
            }
            if (entry.getValue() instanceof Map) {
                flatMap.putAll(flatMap(prefixKey + entry.getKey(), (Map<String, Object>)entry.getValue()));
            }
        }
        return flatMap;
    }
}
