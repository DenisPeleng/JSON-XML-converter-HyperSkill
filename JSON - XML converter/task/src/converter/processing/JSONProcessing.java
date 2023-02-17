package converter.processing;

public class JSONProcessing implements DataProcessing {
    @Override
    public String objectToStr(ObjectWithValue objectWithValue) {
        StringBuilder result = new StringBuilder();
        result.append("{\"" + objectWithValue.getName() + "\" : ");
        if (objectWithValue.getValue() == null) {
            result.append("null }");
        } else {
            result.append("\"" + objectWithValue.getValue() + "\"}");
        }
        return result.toString();
    }

    @Override
    public ObjectWithValue strToObject(String str) {
        String[] jsonStrArr = str.split(":");
        String name = jsonStrArr[0].replace("{\"", "").replace("\"", "").trim();
        String value = jsonStrArr[1].replace("}", "").replace("\"", "").trim();
        return new ObjectWithValue(name, value);
    }
}
