package converter.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLProcessing extends DataProcessing {
    public DataElement strTodataElement(String stringInput) {
        Pattern patternTag = Pattern.compile("<.*?>");
        Pattern patternValueBetween = Pattern.compile("(?<=>).*?(?=<)");
        Pattern patternAttributesTag = Pattern.compile("(?<=\\s).*?\".*?\"");
        Pattern patternElementName = Pattern.compile("(?<=<).*?(?=[ >])");
        Matcher matcherTagOpen = patternTag.matcher(stringInput);
        Matcher matcherValueBetween = patternValueBetween.matcher(stringInput);

        String name = "";
        String value;
        List<DataElement> listAttributes = new ArrayList<>();
        if (matcherTagOpen.find()) {
            String tag = matcherTagOpen.group();
            Matcher matcherAttributesTag = patternAttributesTag.matcher(tag);
            Matcher matcherElementName = patternElementName.matcher(tag);
            while (matcherAttributesTag.find()) {
                String[] attributesParts = matcherAttributesTag.group().split("=");
                String attributeName = attributesParts[0].trim();
                String attributeValue = attributesParts[1].trim();
                listAttributes.add(new DataElement(attributeName, attributeValue));
            }
            if (matcherElementName.find()) {
                name = matcherElementName.group();
            } else throw new IllegalArgumentException();
            if (name.endsWith("/")) {
                name = name.replace("/", "");
                if (listAttributes.isEmpty()) {
                    return new DataElement(name, listAttributes);
                } else {
                    return new DataElement(name);
                }
            }
        }
        if (matcherValueBetween.find()) {
            value = matcherValueBetween.group();
            return new DataElement(name, value, listAttributes);
        } else return new DataElement(name, listAttributes);
    }

    protected String dataElementWithAttributesToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append("<");
        result.append(dataElement.getName());
        if (dataElement.hasAttributes()) {
            for (DataElement tmpAttr : dataElement.getAttributes()
            ) {
                String value = tmpAttr.getValue().startsWith("\"") ? tmpAttr.getValue() : String.format("\"%s\"", tmpAttr.getValue());
                result.append(String.format(" %s = %s", tmpAttr.getName(), value));
            }
        }
        if (!dataElement.hasValue()) {
            result.append("/>");
            return result.toString();
        }
        result.append(">");
        result.append(dataElement.getValue());
        result.append(String.format("</%s>", dataElement.getName()));
        return result.toString();
    }

    protected String dataElementWithValueOnlyToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        if (dataElement.getValue().equals("null")) {
            result.append(String.format("<%s/>", dataElement.getName()));
        } else {
            result.append(String.format("<%s>", dataElement.getName()));
            result.append(dataElement.getValue());
            result.append(String.format("</%s>", dataElement.getName()));
        }
        return result.toString();
    }
}
