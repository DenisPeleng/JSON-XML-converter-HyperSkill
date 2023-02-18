package converter;

import converter.processing.*;
import converter.processing.JSONProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String path = "test.txt";
        Scanner scanner = new Scanner(new File(path));
        DataProcessing dataProcessingInput = new JSONProcessing();
        DataProcessing dataProcessingOutput = new JSONProcessing();
        StringBuilder inputStr = new StringBuilder();
        while (scanner.hasNextLine()){
            inputStr.append(scanner.nextLine().trim());
        }
        String input = inputStr.toString();
        if (input.startsWith("<")) {
            dataProcessingInput = new XMLProcessing();
            dataProcessingOutput = new JSONProcessing();
        } else if (input.startsWith("{")) {
            dataProcessingInput = new JSONProcessing();
            dataProcessingOutput = new XMLProcessing();
        }
        DataElement dataElement = dataProcessingInput.strTodataElement(input);
        String result = dataProcessingOutput.dataElementToStr(dataElement);
        System.out.println(result);
    }
}
