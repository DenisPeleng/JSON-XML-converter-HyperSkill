package converter.processing.json;

import converter.processing.DataElement;

public class ObjectToJSONString {

    public String dataElementWithValueOnlyToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(String.format("\"%s\"", dataElement.getName()));
        result.append(":");
        if (dataElement.hasValue()) {
            result.append(String.format("\"%s\"", dataElement.getValue()));
        } else {
            result.append("null");
        }
        result.append("}");
        return result.toString();
    }

    public String dataElementWithAttributesToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append("{\n");
        result.append(String.format("\t\"%s\" : ", dataElement.getName()));
        if (dataElement.hasAttributes()) {
            result.append("{\n");
            for (DataElement tmpAttr : dataElement.getAttributes()
            ) {
                result.append(String.format("\t\t\"@%s\" : %s,\n", tmpAttr.getName(), tmpAttr.getValue()));
            }
            result.append(String.format("\t\t\"#%s\" : ", dataElement.getName()));
            if (dataElement.hasValue()) {
                result.append(String.format("\"%s\"", dataElement.getValue()));
            } else {
                result.append("null");
            }
            result.append("\n\t}\n");
        }
        result.append("}");
        return result.toString();
    }
}
