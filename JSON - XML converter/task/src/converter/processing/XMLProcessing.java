package converter.processing;

public class XMLProcessing implements DataProcessing {
    @Override
    public String objectToStr(ObjectWithValue objectWithValue) {
        StringBuilder result = new StringBuilder();
        if (objectWithValue.getValue().equals("null")) {
            result.append("<" + objectWithValue.getName() + "/>");
        } else {
            result.append("<" + objectWithValue.getName() + ">");
            result.append(objectWithValue.getValue());
            result.append("</" + objectWithValue.getName() + ">");
        }
        return result.toString();
    }

    @Override
    public ObjectWithValue strToObject(String str) {
        String name = str.substring(str.indexOf("<") + 1, str.indexOf(">"));
        if (name.contains("/")) {
            name = name.replace("/", "");
            return new ObjectWithValue(name);
        } else {
            String value = str.substring(str.indexOf(">") + 1);
            value = value.substring(0, value.indexOf("<"));
            return new ObjectWithValue(name, value);
        }

    }
}
