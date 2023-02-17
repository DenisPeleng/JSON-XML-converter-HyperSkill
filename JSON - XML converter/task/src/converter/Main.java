package converter;

import converter.processing.*;
import converter.processing.JSONProcessing;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        DataProcessing dataProcessingInput = new JSONProcessing();
        DataProcessing dataProcessingOutput = new JSONProcessing();
        if (input.startsWith("<")) {
            dataProcessingInput = new XMLProcessing();
            dataProcessingOutput = new JSONProcessing();
        } else if (input.startsWith("{")) {
            dataProcessingInput = new JSONProcessing();
            dataProcessingOutput = new XMLProcessing();
        }
        ObjectWithValue objectWithValue = dataProcessingInput.strToObject(input);
        String result = dataProcessingOutput.objectToStr(objectWithValue);
        System.out.println(result);
    }
}
