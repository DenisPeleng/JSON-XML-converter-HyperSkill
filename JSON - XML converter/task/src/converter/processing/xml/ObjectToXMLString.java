package converter.processing.xml;

import converter.processing.DataElement;

import java.util.*;

public class ObjectToXMLString {
    private static final ArrayDeque<String> closeTags = new ArrayDeque<>();
    private static final HashMap<String, String> tagsValues = new HashMap<>();
    private static String currentTag = "";
    private static List<String> previousPath;
    private static int rootElements = 0;

    public static String objectToXmlStringConversion(List<DataElement> dataElementList) {
        StringBuilder result = new StringBuilder();
        for (DataElement dataElement : dataElementList
        ) {
            result.append(processCurrentElement(dataElement));
        }
        while (!closeTags.isEmpty()) {
            result.append(tagsValues.get(closeTags.pop()));
        }
        if (rootElements > 1) {
            result.append("</root>");
            result.insert(0, "<root>");
        }
        return result.toString();
    }

    private static String processCurrentElement(DataElement dataElement) {
        StringBuilder resultElementInStr = new StringBuilder();
        if (currentTag.isEmpty()) {
            currentTag = dataElement.getName();
        }
        if (previousPath == null) {
            rootElements++;
            previousPath = new ArrayList<>(Arrays.asList(dataElement.getPath().split(",")));
        }
        List<String> currentPath = List.of(dataElement.getPath().split(","));
        while (!closeTags.isEmpty() && (!isSamePath(previousPath, currentPath) || previousPath.isEmpty())) {
            resultElementInStr.append(tagsValues.get(closeTags.pop()));
            if (!previousPath.isEmpty()) {
                previousPath.remove(previousPath.size() - 1);
            }
        }
        if (previousPath.isEmpty()) {
            rootElements++;
        }
        currentTag = dataElement.getName();
        previousPath = new ArrayList<>(Arrays.asList(dataElement.getPath().split(",")));

        if (dataElement.hasAttributes()) {
            resultElementInStr.append(dataElementWithAttributesToStr(dataElement));
        } else {
            resultElementInStr.append(dataElementWithValueOnlyToStr(dataElement));
        }
        return resultElementInStr.toString();
    }

    private static String dataElementWithAttributesToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append("<");
        result.append(dataElement.getName());
        if (dataElement.hasAttributes()) {
            for (DataElement tmpAttr : dataElement.getAttributes()
            ) {
                result.append(String.format(" %s=%s", tmpAttr.getName(), tmpAttr.getValue()));
            }
        }
        if (!dataElement.hasValue() || dataElement.getValue().equals("null")) {
            tagsValues.put(dataElement.getName(), " />");
            closeTags.push(dataElement.getName());
            return result.toString();
        }
        result.append(">");
        result.append(dataElement.getValueWithoutBrackets());
        tagsValues.put(dataElement.getName(), String.format("</%s>", dataElement.getName()));
        closeTags.push(dataElement.getName());
        return result.toString();
    }

    private static String dataElementWithValueOnlyToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        if (dataElement.hasValue()) {
            if (dataElement.getValue().equals("null")) {
                result.append(String.format("<%s", dataElement.getName()));
                tagsValues.put(dataElement.getName(), " />");
                closeTags.push(dataElement.getName());
            } else {
                result.append(String.format("<%s>", dataElement.getName()));
                result.append(dataElement.getValueWithoutBrackets());
                tagsValues.put(dataElement.getName(), String.format("</%s>", dataElement.getName()));
                closeTags.push(dataElement.getName());
            }
        } else {
            result.append(String.format("<%s>", dataElement.getName()));
            tagsValues.put(dataElement.getName(), String.format("</%s>", dataElement.getName()));
            closeTags.push(dataElement.getName());
        }
        return result.toString();
    }

    private static boolean isSamePath(List<String> previousPath, List<String> currentPath) {
        int smallestArrSize = Math.min(currentPath.size(), previousPath.size());
        for (int i = 0; i < smallestArrSize; i++) {
            if (!previousPath.get(i).equals(currentPath.get(i))) {
                return false;
            }
        }
        return true;
    }
}
