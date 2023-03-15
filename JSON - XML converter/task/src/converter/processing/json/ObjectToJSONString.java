package converter.processing.json;

import converter.processing.DataElement;

import java.util.*;

public class ObjectToJSONString {
    private static final ArrayDeque<String> closeBrackets = new ArrayDeque<>();
    private static final ArrayDeque<List<String>> closeBracketsInner = new ArrayDeque<>();
    private static String currentTag = "";
    private static List<String> previousPath;

    public static String objectToJsonStringConversion(List<DataElement> dataElementList) {
        StringBuilder result = new StringBuilder();
        for (DataElement dataElement : dataElementList
        ) {
            result.append(processCurrentElement(dataElement));
        }
        while (!closeBrackets.isEmpty()) {
            result.append(closeBrackets.pop());
        }
        while (!closeBracketsInner.isEmpty()) {
            result.append("}");
            closeBracketsInner.pop();
        }
        return result.toString();
    }

    private static String processCurrentElement(DataElement dataElement) {
        StringBuilder resultElementInStr = new StringBuilder();
        if (currentTag.isEmpty()) {
            currentTag = dataElement.getName();
        }
        if (previousPath == null) {
            previousPath = new ArrayList<>(Arrays.asList(dataElement.getPath().split(",")));
        }

        List<String> currentPath = List.of(dataElement.getPath().split(","));
        List<String> previousPathAttr = new ArrayList<>(previousPath);
        if (isSamePath(previousPath, currentPath)) {
            resultElementInStr.append("{");
            closeBracketsInner.push(currentPath);
        }
        while (!closeBrackets.isEmpty() && (!isSamePath(previousPathAttr, currentPath) || previousPath.isEmpty())) {
            resultElementInStr.append(closeBrackets.pop());
            if (!previousPathAttr.isEmpty()) {
                previousPathAttr.remove(previousPathAttr.size() - 1);
            }
        }
        if (isSameParentTag(previousPath, currentPath)) {
            resultElementInStr.append(",");
        }
        while (!closeBracketsInner.isEmpty() && !isSameParentTag(closeBracketsInner.peek(), currentPath) && !isSamePath(previousPath, currentPath)) {
            resultElementInStr.append("}");
            closeBracketsInner.pop();
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

    private static String dataElementWithValueOnlyToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("\"%s\"", dataElement.getName()));
        result.append(":");
        if (!dataElement.isInvalidAttr()) {
            result.append(String.format("%s", dataElement.getValue()));
        }
        return result.toString();
    }

    private static String dataElementWithAttributesToStr(DataElement dataElement) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("\"%s\":", dataElement.getName()));
        if (dataElement.hasAttributes()) {
            result.append("{");
            for (DataElement tmpAttr : dataElement.getAttributes()
            ) {
                result.append(String.format("\"@%s\":%s,", tmpAttr.getName(), tmpAttr.getValue()));
            }
            result.append(String.format("\"#%s\":", dataElement.getName()));
            if (!dataElement.isInvalidAttr()) {
                result.append(String.format("%s", dataElement.getValue()));
            }
            closeBrackets.push("}");
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

    private static boolean isSameParentTag(List<String> previousPath, List<String> currentPath) {
        if (currentPath.size() >= 2 && previousPath.size() >= 2) {
            return previousPath.get(previousPath.size() - 2).equals(currentPath.get(currentPath.size() - 2));

        } else return false;
    }
}
