package converter.processing;

import java.util.ArrayList;
import java.util.List;

public class DataElement {
    private final String name;
    private String value;
    private String path;
    private boolean isInvalidAttr;
    private List<DataElement> attributes;

    public DataElement(String name, String value, List<DataElement> attribute) {
        this.name = name;
        if (!value.equals("null") && !value.contains("\"")) {
            this.value = String.format("\"%s\"", value);
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

    public DataElement(String name, String value) {
        this.name = name;
        if (!value.equals("null") && !value.contains("\"")) {
            this.value = String.format("\"%s\"", value);
        } else {
            this.value = value;
        }
        this.attributes = new ArrayList<>();
    }

    public DataElement(String name) {
        this.name = name;
        this.value = "";
        this.attributes = new ArrayList<>();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setValue(String value) {
        if (value == null) {
            this.value = null;
        } else if (!value.equals("null") && !value.contains("\"")) {
            this.value = String.format("\"%s\"", value);
        } else {
            this.value = value;
        }

    }

    public void setInvalidAttr(boolean invalidAttr) {
        isInvalidAttr = invalidAttr;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getValueWithoutBrackets() {
        return value.replace("\"", "");
    }

    public List<DataElement> getAttributes() {
        return attributes;
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public boolean hasValue() {
        if (value == null) {
            return false;
        } else {
            String tmpValue = value.replace("\"", "");
            return !tmpValue.isBlank();
        }

    }

    public void printDataElementDescription() {
        System.out.println("Element:");
        System.out.printf("path = %s\n", path);
        if (value != null) {
            System.out.printf("value = %s\n", value);
        }
        if (!attributes.isEmpty()) {
            System.out.println("attributes:");
            for (DataElement tmpAttr : attributes
            ) {
                System.out.printf("%s = %s\n", tmpAttr.getName(), tmpAttr.getValue());
            }
        }

        System.out.println();
    }

    public boolean isInvalidAttr() {
        return isInvalidAttr;
    }

    public String getPath() {
        return path;
    }

    public void setAttributes(List<DataElement> attributes) {
        this.attributes = attributes;
    }

}
