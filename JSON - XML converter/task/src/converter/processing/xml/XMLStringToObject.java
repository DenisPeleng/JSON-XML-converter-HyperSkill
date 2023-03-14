package converter.processing.xml;

import converter.processing.DataElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLStringToObject {
    private static final LinkedHashMap<String, String> tagsWithPath = new LinkedHashMap<>();
    static LinkedHashMap<String, String>  checkInsideValue(String currentValue, String deepPath) {
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
        return tagsWithPath;
    }
    private static boolean hasOneTagProperty(String strInput) {
        Pattern patternTag = Pattern.compile("<.*?>");
        Matcher matcherTagOpen = patternTag.matcher(strInput);
        if (matcherTagOpen.find()) {
            return matcherTagOpen.group().contains("/>");
        }
        return false;
    }

    private static DataElement parseCurrentTag(String newTagName, String data) {
        Pattern newFullTag = Pattern.compile(String.format("<%s.*?<\\/%s>", newTagName, newTagName));
        Matcher matcherFullTag = newFullTag.matcher(data);
        String valueTMP = "";
        if (matcherFullTag.find()) {
            valueTMP = matcherFullTag.group();
        }
        return strTagTodataElement(valueTMP);
    }
    public static DataElement strTagTodataElement(String stringInput) {
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
}
