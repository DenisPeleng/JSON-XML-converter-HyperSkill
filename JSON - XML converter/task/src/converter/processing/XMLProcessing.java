package converter.processing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLProcessing implements DataProcessing {
    private final List<DataElement> resultDataElements = new ArrayList<>();
    private final LinkedHashMap<String, String> tagsWithPath = new LinkedHashMap<>();

    public DataElement strTagTodataElement(String stringInput) {
        Pattern patternTag = Pattern.compile("<.*?>");
        Pattern patternValueBetween = Pattern.compile("(?<=>).*(?=</)");
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

    @Override
    public List<DataElement> parseAllDataElement(String strInput) {
        checkInsideValue(strInput, "");
        for (
                String tag : tagsWithPath.keySet()
        ) {
            DataElement tagDataElement = strTagTodataElement(tag);
            tagDataElement.setPath(tagsWithPath.get(tag));
            if (tagDataElement.getValue().startsWith("<")) {
                tagDataElement.setValue("");
            }
            resultDataElements.add(tagDataElement);
        }
        return resultDataElements;
    }

    private void checkInsideValue(String currentValue, String deepPath) {
        if (currentValue.startsWith("<")) {
            Pattern patternNewTagName = Pattern.compile("(?<=<)[^/].*?(?=[ >])");
            Matcher matcherNewTagName = patternNewTagName.matcher(currentValue);

            while (matcherNewTagName.find()) {
                String newTagName = matcherNewTagName.group();
                Pattern newFullTag = Pattern.compile(String.format("<%s.*?\\/>", newTagName));
                Matcher matcherFullTag = newFullTag.matcher(currentValue);
                if (!hasOneTagProperty(currentValue)) {
                    newFullTag = Pattern.compile(String.format("<%s.*?<\\/%s>", newTagName, newTagName));
                    matcherFullTag = newFullTag.matcher(currentValue);
                }
                if (matcherFullTag.find()) {
                    String currentTagOnly = matcherFullTag.group();
                    DataElement tmpDataElement = parseCurrentTag(newTagName, currentTagOnly);
                    String currentDeepPath = String.format("%s, %s", deepPath, newTagName);
                    if (deepPath.isEmpty()) {
                        currentDeepPath = String.format("%s", newTagName);
                    }
                    tagsWithPath.put(currentTagOnly, currentDeepPath);
                    checkInsideValue(tmpDataElement.getValue(), currentDeepPath);
                    currentValue = currentValue.replaceFirst(currentTagOnly, "");
                    matcherNewTagName = patternNewTagName.matcher(currentValue);
                }


            }
        }
    }

    private boolean hasOneTagProperty(String strInput) {
        Pattern patternTag = Pattern.compile("<.*?>");
        Matcher matcherTagOpen = patternTag.matcher(strInput);
        if (matcherTagOpen.find()) {
            return matcherTagOpen.group().contains("/>");
        }
        return false;
    }

    private DataElement parseCurrentTag(String newTagName, String data) {
        Pattern newFullTag = Pattern.compile(String.format("<%s.*?<\\/%s>", newTagName, newTagName));
        Matcher matcherFullTag = newFullTag.matcher(data);
        String valueTMP = "";
        if (matcherFullTag.find()) {
            valueTMP = matcherFullTag.group();
        }
        return strTagTodataElement(valueTMP);
    }

    public String dataElementWithAttributesToStr(DataElement dataElement) {
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

    public String dataElementWithValueOnlyToStr(DataElement dataElement) {
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
