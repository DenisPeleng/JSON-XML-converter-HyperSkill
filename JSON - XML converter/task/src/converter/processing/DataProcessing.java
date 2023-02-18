package converter.processing;

public abstract class DataProcessing {
    public String dataElementToStr(DataElement dataElement) {
        if (dataElement.hasAttributes()) {
            return dataElementWithAttributesToStr(dataElement);
        } else return dataElementWithValueOnlyToStr(dataElement);
    }

    protected abstract String dataElementWithValueOnlyToStr(DataElement dataElement);

    protected abstract String dataElementWithAttributesToStr(DataElement dataElement);

    public abstract DataElement strTodataElement(String str);
}

