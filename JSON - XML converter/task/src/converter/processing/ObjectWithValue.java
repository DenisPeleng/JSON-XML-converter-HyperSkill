package converter.processing;

public class ObjectWithValue {
    private String name;
    private String value;

    ObjectWithValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    ObjectWithValue(String name) {
        this.name = name;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
