package converter.processing.json;

import converter.processing.DataElement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONStringToObject {
    private static final List<DataElement> resultDataElements = new ArrayList<>();

    static void checkJsonItem(String valueToParse, DataElement parentDataElement) {
        Pattern patternNewTagName = Pattern.compile("(?<=\").*?(?=\":)");
        Matcher matcherNewTagName = patternNewTagName.matcher(valueToParse);
        String value = "";
        boolean isAttr = false;
        boolean isValueParent = false;
        if (matcherNewTagName.find()) {
            String newTagName = matcherNewTagName.group().replace("\"", "");
            if (newTagName.isEmpty()) {
                parentDataElement.setInvalidAttr(true);
                return;
            }
            if (newTagName.startsWith("@")) {
                if (newTagName.length() > 1) {
                    isAttr = true;
                } else {
                    parentDataElement.setInvalidAttr(true);
                    return;
                }
            }
            String currentDeepPath = String.format("%s, %s", parentDataElement.getPath(), newTagName);
            if (parentDataElement.isInvalidAttr() && newTagName.startsWith("#") && newTagName.length() == 1) {
                parentDataElement.setValue(null);
                return;
            }
            if (!parentDataElement.isInvalidAttr() && newTagName.startsWith("#") && newTagName.substring(1).equals(parentDataElement.getName())) {
                isValueParent = true;
            } else if (parentDataElement.isInvalidAttr()) {
                newTagName = newTagName.replace("#", "");
            } else if (newTagName.startsWith("#") && !newTagName.substring(1).equals(parentDataElement.getName())) {
                List<DataElement> attrs = parentDataElement.getAttributes();
                parentDataElement.setAttributes(new ArrayList<>());
                parentDataElement.setInvalidAttr(true);
                for (DataElement tmpElement : attrs
                ) {
                    tmpElement.setPath(parentDataElement.getPath() + ", " + tmpElement.getName());
                    resultDataElements.add(tmpElement);
                }
                newTagName = newTagName.replace("#", "");
            }

            if (parentDataElement.getPath().isEmpty()) {
                currentDeepPath = String.format("%s", newTagName);
            }
            Pattern patternNewTagValue = Pattern.compile("(?<=:).*");
            Matcher matcherNewTagValue = patternNewTagValue.matcher(valueToParse);
            if (matcherNewTagValue.find()) {
                value = matcherNewTagValue.group().trim();
            }

            if (value.startsWith("{")) {
                if (isValueParent) {
                    DataElement tmpDataElement = new DataElement(newTagName);
                    tmpDataElement.setPath(currentDeepPath);
                    checkBrackets(value, tmpDataElement);

                } else {
                    newTagName = newTagName.replace("@", "");
                    newTagName = newTagName.replace("#", "");
                    DataElement tmpDataElement = new DataElement(newTagName);
                    currentDeepPath = currentDeepPath.replace("@", "");
                    currentDeepPath = currentDeepPath.replace("#", "");
                    tmpDataElement.setPath(currentDeepPath);
                    tmpDataElement.setValue("");

                    resultDataElements.add(tmpDataElement);
                    checkBrackets(value, tmpDataElement);
                }
            } else {
                if (isAttr) {
                    List<DataElement> attrs = parentDataElement.getAttributes();
                    if (value.equals("null")) {
                        value = "";
                    }
                    DataElement currentAttr = new DataElement(newTagName.replace("@", ""), value);
                    attrs.add(currentAttr);
                    parentDataElement.setAttributes(attrs);
                    return;
                }
                if (isValueParent) {
                    parentDataElement.setValue(value);
                    return;
                }
                if (parentDataElement.hasAttributes() || parentDataElement.hasValue()) {
                    List<DataElement> attr = parentDataElement.getAttributes();
                    parentDataElement.setAttributes(new ArrayList<>());
                    for (DataElement tmpAttr : attr
                    ) {
                        tmpAttr.setPath(parentDataElement.getPath() + ", " + tmpAttr.getName());
                        resultDataElements.add(tmpAttr);
                    }
                    if (parentDataElement.getValue().length() > 2) {
                        DataElement tmpNewDataElement = new DataElement(parentDataElement.getName(), parentDataElement.getValue());
                        tmpNewDataElement.setPath(parentDataElement.getPath() + ", " + tmpNewDataElement.getName());
                        resultDataElements.add(tmpNewDataElement);
                        parentDataElement.setValue("");
                    }

                    parentDataElement.setInvalidAttr(true);
                }
                DataElement tmpDataElement = new DataElement(newTagName, value);
                currentDeepPath = currentDeepPath.replace("#", "");
                tmpDataElement.setPath(currentDeepPath);
                resultDataElements.add(tmpDataElement);
            }
        } else {
            parentDataElement.setValue("");
        }


    }

    public static void checkBrackets(String inputStr, DataElement parentDataElement) {
        Pattern newFullTag = Pattern.compile("(?<=\\{).*(?=})");
        Matcher matcherFullTag = newFullTag.matcher(inputStr);
        if (!inputStr.contains("#")) {
            parentDataElement.setInvalidAttr(true);
        }
        while (matcherFullTag.find()) {
            String valueToParse = matcherFullTag.group().trim();
            if (!valueToParse.endsWith(",")) {
                valueToParse = valueToParse + ",";
            }
            List<String> jsonElements = parseJsonElements(valueToParse);
            for (String tmpElement : jsonElements
            ) {
                checkJsonItem(tmpElement, parentDataElement);
            }
        }
    }

    private static List<String> parseJsonElements(String fullString) {
        List<String> jsonElements = new ArrayList<>();
        char[] json = fullString.toCharArray();
        int currentElementStart = 0;
        boolean isBracketsOpened = false;
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < json.length; i++) {
            if ((json[i] == ',') && !isBracketsOpened) {
                String tmpElement = fullString.substring(currentElementStart, i);
                jsonElements.add(tmpElement);
                currentElementStart = i + 1;
            }
            if (json[i] == '{') {
                deque.push(i);
                isBracketsOpened = true;
            } else if (json[i] == '}' && !deque.isEmpty()) {
                deque.pop();
                if (deque.isEmpty()) {
                    isBracketsOpened = false;
                }
            }

        }
        return jsonElements;
    }

    public static List<DataElement> parseAllDataElements(String str) {
        DataElement startElement = new DataElement("");
        startElement.setPath("");
        checkBrackets(str, startElement);
        DataElement previousElement = null;
        removeTagDuplicatesFromAllList();
        for (int i = 0; i < resultDataElements.size(); i++) {
            DataElement tmpElement = resultDataElements.get(i);
            if (tmpElement.isInvalidAttr() && tmpElement.hasAttributes()) {
                List<DataElement> attr = tmpElement.getAttributes();
                tmpElement.setAttributes(new ArrayList<>());
                for (DataElement tmpAttr : attr
                ) {
                    tmpAttr.setPath(tmpElement.getPath() + ", " + tmpAttr.getName());
                    resultDataElements.add(i + 1, tmpAttr);
                }
            }
            if (previousElement != null && previousElement.getPath().equals(tmpElement.getPath())) {
                if (previousElement.getPath().equals(tmpElement.getPath())) {
                    resultDataElements.remove(previousElement);
                }
            }
            previousElement = tmpElement;

        }

        return resultDataElements;
    }
    private static void removeTagDuplicatesFromAllList(){
        for (int i = 0; i < resultDataElements.size(); i++) {
            DataElement currentElement = resultDataElements.get(i);
            String path = currentElement.getPath();
            for (int j = 0; j < resultDataElements.size(); j++) {
                DataElement secondCurrentElement = resultDataElements.get(j);
                String secondPath = secondCurrentElement.getPath();
                if (secondPath.equals(path) && i != j) {
                    resultDataElements.remove(i);
                    i--;
                    break;
                }
            }
        }
    }
}
