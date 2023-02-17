package converter.processing;

public interface DataProcessing {
    String objectToStr(ObjectWithValue objectWithValue);
    ObjectWithValue strToObject(String str);
}
