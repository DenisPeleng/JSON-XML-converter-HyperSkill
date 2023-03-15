package converter.processing.xml;

import converter.processing.DataElement;
import converter.processing.DataProcessing;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static converter.processing.xml.ObjectToXMLString.*;
import static converter.processing.xml.XMLStringToObject.*;

public class XMLProcessing implements DataProcessing {
    private final List<DataElement> resultDataElements = new ArrayList<>();

    @Override
    public List<DataElement> parseAllDataElement(String strInput) {
        LinkedHashMap<String, String> tagsWithPath = checkInsideValue(strInput, "");
        for (
                String tag : tagsWithPath.keySet()
        ) {
            DataElement tagDataElement = strTagTodataElement(tag);
            tagDataElement.setPath(tagsWithPath.get(tag));
            if (tagDataElement.getValue().startsWith("<")) {
                tagDataElement.setInvalidAttr(true);
            }
            resultDataElements.add(tagDataElement);
        }
        return resultDataElements;
    }

    @Override
    public String allDataElementsToStr(List<DataElement> allDataElements) {
        return objectToXmlStringConversion(allDataElements);
    }


}
