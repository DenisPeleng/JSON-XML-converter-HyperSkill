package converter.processing.json;

import converter.processing.DataElement;
import converter.processing.DataProcessing;

import java.util.List;

import static converter.processing.json.JSONStringToObject.parseAllDataElements;
import static converter.processing.json.ObjectToJSONString.objectToJsonStringConversion;

public class JSONProcessing implements DataProcessing {

    @Override
    public List<DataElement> parseAllDataElement(String str) {
        return parseAllDataElements(str);
    }

    @Override
    public String allDataElementsToStr(List<DataElement> allDataElements) {
        return objectToJsonStringConversion(allDataElements);

    }


}
