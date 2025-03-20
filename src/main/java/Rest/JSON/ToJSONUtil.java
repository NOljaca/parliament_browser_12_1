package Rest.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Helper-class for creating JSON-Objects of complex maps for javascript-compatibility.
 * @author Amal
 */
public class ToJSONUtil {

    public ToJSONUtil() {
    }

    /**
     * This method creates a JSONArray from a given list of maps.
     * @param list list which needs to be converted to a JSONArray
     * @param firstKey key for first value
     * @param secondKey key for second value
     * @return JSONArray
     * @throws JSONException
     *
     * @author Amal
     */
    public static JSONArray toJSONArray(List<Map<String, Object>> list, String firstKey, String secondKey) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> map : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(firstKey, map.get(firstKey));
            jsonObject.put(secondKey, map.get(secondKey));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /**
     * This method converts a given map to a JSONObject for javascript compatibility
     * @param map map which needs to be converted to a JSONObject
     * @return JSONObject
     * @throws JSONException
     *
     * @author Amal
     */
    public static JSONObject toJSONObject(Map<String, Object> map) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }
}
