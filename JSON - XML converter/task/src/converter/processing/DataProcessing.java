package converter.processing;

import java.util.List;

public interface DataProcessing {
    default String dataElementToStr(DataElement dataElement) {
        if (dataElement.hasAttributes()) {
            return dataElementWithAttributesToStr(dataElement);
        } else return dataElementWithValueOnlyToStr(dataElement);
    }

   String dataElementWithValueOnlyToStr(DataElement dataElement);

   String dataElementWithAttributesToStr(DataElement dataElement);

    List<DataElement> parseAllDataElement(String str);
}

