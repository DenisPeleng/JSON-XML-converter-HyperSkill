package converter.processing;

import java.util.List;

public interface DataProcessing {
    List<DataElement> parseAllDataElement(String str);

    String allDataElementsToStr(List<DataElement> allDataElements);
}

