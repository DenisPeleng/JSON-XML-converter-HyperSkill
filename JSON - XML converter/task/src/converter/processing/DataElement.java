package converter.processing;

import java.util.ArrayList;
import java.util.List;

public class DataElement {
    private final String name;
    private final String value;
    private final List<DataElement> attributes;

    public DataElement(String name, String value, List<DataElement> attribute) {
        this.name = name;
        if (value.equals("null")) {
            this.value = "";
        } else {
            this.value = value;
        }
        this.attributes = attribute;
    }

    public DataElement(String name, List<DataElement> attribute) {
        this.name = name;
        this.value = "";
        this.attributes = attribute;
    }

    DataElement(String name, String value) {
        this.name = name;
        if (value.equals("null")) {
            this.value = "";
        } else {
            this.value = value;
        }
        this.attributes = new ArrayList<>();
    }

    DataElement(String name) {
        this.name = name;
        this.value = "";
        this.attributes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<DataElement> getAttributes() {
        return attributes;
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public boolean hasValue() {
        return !value.isBlank();
    }
}
