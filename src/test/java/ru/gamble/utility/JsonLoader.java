package ru.gamble.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.TestDataProvider;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.datajack.providers.json.JsonDataProvider;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public class JsonLoader {
    private static TestDataProvider data;
    private static final Logger LOG = LoggerFactory.getLogger(JsonLoader.class);
    private static final String sep = File.separator;

    public static TestDataProvider getData() {
            try {
                data = new JsonDataProvider("src" + sep + "test" + sep + "resources", "data");
            } catch (DataException e) {
                LOG.error(e.getMessage());
            }
        return data;
    }

    /**
     * Метод пробегает по Map of Maps и ищет ключ
     * и возвращает либо значение по искомогу ключу или null
     *
     * @param finding - искомый ключ
     * @param json     - Object
     */
    public static Object hashMapper(Object json, String finding) {
        String key;
        Object request = null, value;

        if (json instanceof Map) {
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Object> map = oMapper.convertValue(json, Map.class);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                if ((value instanceof String) || (value instanceof Integer) || (value instanceof Long) || (value instanceof Timestamp) || (value instanceof Boolean)) {
                    if (key.equalsIgnoreCase(finding)) {
                        return request = value;
                    }
                } else if (value instanceof Map) {
                    Map<String, Object> subMap = (Map<String, Object>) value;
                    if (key.equalsIgnoreCase(finding)) {
                        return request = value;
                    }else{
                        request = hashMapper(subMap, finding);
                    }
                } else if (value instanceof List) {
                    List list = (List) value;
                    if (key.equalsIgnoreCase(finding)) {
                        return request = list;
                    } else {
                        request = hashMapper(list, finding);
                    }
                }
                //**************Пробная обработка
                else  if(value == null){
                    if (key.equalsIgnoreCase(finding)) {
                        return null;
                    }
                }
                //******************************
                else {
                    throw new IllegalArgumentException(String.valueOf(value));
                }
            }
        }else if(json instanceof List){
            for (int i = 0; i < ((List) json).size(); i++) {
                Object listItem = ((List) json).get(i);
                request = hashMapper(listItem, finding);
            }
        }
        return request;
    }
}
