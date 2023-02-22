package converter.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONProcessing implements DataProcessing {
    public DataElement strTagTodataElement(String str) {
        List<DataElement> listAttributes = new ArrayList<>();
        String firstValues = getValueInsideBrackets(str);
        String name = firstValues.substring(0, firstValues.indexOf(":")).replace("\"", "").trim();

        String value = firstValues.substring(firstValues.indexOf(":") + 2).trim();
        if (value.startsWith("{")) {
            String[] attributes = getValueInsideBrackets(value).split(",");
            for (String attr : attributes
            ) {
                String[] attrArr = attr.split(":");
                String attrName = attrArr[0].trim().replace("\"", "");
                String attrValue = attrArr[1].trim();
                if (attrName.startsWith("@")) {
                    attrName = attrName.substring(1);
                    listAttributes.add(new DataElement(attrName, attrValue));
                } else if (attrName.startsWith("#")) {
                    value = attrValue.replace("\"", "");
                }
            }
            return new DataElement(name, value, listAttributes);
        } else {
            return new DataElement(name, value);
        }
    }

    @Override
    public List<DataElement> parseAllDataElement(String str) {
        return null;
    }

    private String getValueInsideBrackets(String stringInput) {
        Pattern patternInsideBrackets = Pattern.compile("(?<=\\{).*(?=})");
        Matcher matcherInsideBrackets = patternInsideBrackets.matcher(stringInput);
        if (matcherInsideBrackets.find()) {
            return matcherInsideBrackets.group();
        }
        return "";
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
}
